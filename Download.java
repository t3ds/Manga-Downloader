
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.ArrayList;


public class Download extends Application
{
    float last_pg=1;
    private static ChromeDriver driver;
    Label progress=new Label();
    ProgressBar pb=new ProgressBar();
    ListView ListChapters=new ListView();
    ArrayList<String> chapterNames = new ArrayList<>();
    String mangaName;
    private Process manga;
   // WebDriver driver;

    {
        manga = new Process();
    }

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);

        driver = new ChromeDriver(options);

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Creates Primary Window
        primaryStage.setTitle("Manga Downloader");


        primaryStage.setOnCloseRequest( e -> Shutdown(primaryStage));

        //ERROR PAGE
        Stage errorStage=new Stage();
        errorStage.setTitle("ERROR!");
        errorStage.initModality(Modality.APPLICATION_MODAL);
        errorStage.setMinWidth(250);
        errorStage.setMinHeight(250);
        Label error=new Label("PLEASE SELECT AT LEAST 1 CHAPTER");
        Label error2=new Label("NO MANGA FOUND");
        Button closeError=new Button("Okay");
        closeError.setOnAction(e->errorStage.close());

        VBox errorLayout=new VBox(10);
        errorLayout.setAlignment(Pos.CENTER);
        errorLayout.getChildren().addAll(error,closeError);
        Scene errorScene=new Scene(errorLayout);

        VBox mangaNotFound=new VBox(10);
        mangaNotFound.setAlignment(Pos.CENTER);
        mangaNotFound.getChildren().addAll(error2,closeError);
        Scene mangaError=new Scene(mangaNotFound);



//Parameters for Window 1(Entering the name of the manga)
        Label name= new Label("Enter The Name of the Manga");
        TextField mangaName=new TextField();
        Button chapterConfirm=new Button("Submit");
        ComboBox<String> chapters=new ComboBox<>();
        chapters.setPromptText("Choose the manga");
        chapters.setPrefWidth(300);

        mangaName.setPromptText("eg. Naruto");
        Button enterName=new Button();

        VBox layout=new VBox();
        layout.setPadding(new Insets(10,10,10,10));
        layout.setSpacing(10);layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(name,mangaName,enterName);

//Creates the first Window
        Scene scene=new Scene(layout,300,100);
        primaryStage.setScene(scene);
        primaryStage.show();


//Creates Second Window
        BorderPane border=new BorderPane();

        HBox chapterPane=new HBox();
        chapterPane.setPadding(new Insets(20, 20, 20, 20));
        chapterPane.setSpacing(10);
        chapterPane.setAlignment(Pos.CENTER);
        chapterPane.getChildren().addAll(chapters,chapterConfirm);

        Button confirmDownload=new Button("Download");
        Button back=new Button("Back");

        GridPane grid= new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(confirmDownload,15,0);
        grid.add(back,1,1);
        grid.add(pb,15,1);
        grid.add(progress,16,1);

        VBox cen=new VBox();
        cen.setPadding(new Insets(20,20,20,20));
        ListChapters.prefWidth(cen.getWidth());
        cen.getChildren().add(ListChapters);

        border.setTop(chapterPane);
        border.setCenter(cen);
        border.setBottom(grid);
        //border.getBottom().prefHeight(120);
        Scene mainPage=new Scene(border,600,400);
        primaryStage.setResizable(false);



       // ListChapters.setPadding(new Insets(0, 20, 0, 60));

        ListChapters.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);





//Takes the user input and passes control to manga to find appropriate comic
        enterName.setText("Enter");
        enterName.setOnAction( e -> {
            try {
               String chapterList[]=new String[manga.optionSize];
               chapterList=manga.listOptions(mangaName.getText());

                if(chapterList.length==0)
                {
                    errorStage.setScene(mangaError);
                    errorStage.show();
                }

                for (int i=0;i<chapterList.length;i++)
                {
                    System.out.println(chapterList[i]);
                    chapters.getItems().add(chapterList[i]);

                    primaryStage.setScene(mainPage);
                    primaryStage.show();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

//Confirms the chapter
        chapterConfirm.setOnAction(e-> {
            try {
                getManga(chapters);
            } catch (IOException e1) {

            }
        });


//Goes back to previous page
        back.setOnAction(e-> {
            primaryStage.setScene(scene);
            chapterNames.clear();
            manga.chapterHref.clear();
            chapters.setValue(null);
            ListChapters.getItems().clear();
            chapters.setPromptText("Choose the manga");
            if(manga.chapterNames!=null) {
                manga.chapterNames.clear();
            }
        });


//Downloads the chapter of the manga needed
        confirmDownload.setOnAction(e-> {
            //ListChapters.setOnMouseClicked(event -> {
                ObservableList<String> selectedItems =  ListChapters.getSelectionModel().getSelectedItems();
                String a[]=new String[selectedItems.size()];
                if (selectedItems.isEmpty()==true)
                {
                    errorStage.setScene(errorScene);
                    errorStage.show();
                }
                else {
                    for (String s : selectedItems) {
                        initiateDownload(s,grid);
                    }

                }

           // });
        });


    }
//Shuts the window
    private void Shutdown(Stage window)
    {
        window.close();
    }


    private void getManga(ComboBox<String> chapters) throws IOException {
         mangaName=chapters.getValue();

        chapterNames = manga.getChapters(mangaName);
        //ListChapters.getItems().remove(0);
        for (String c : chapterNames) {
            System.out.println(c);
            ListChapters.getItems().add(c);
        }

    }


    private void initiateDownload(String s,GridPane grid)
    {

                int i=1,flag=1;
                int index=(chapterNames).indexOf(s);

                //Downloader d = new Downloader(mangaName, chapterNames.get(index), manga.chapterHref.get(index), i,driver);

        //mangaName, chapterNames.get(index), manga.chapterHref.get(index), i


            Task<Void> task=new Downloader(mangaName, chapterNames.get(index), manga.chapterHref.get(index), i,driver);
            pb.progressProperty().bind(task.progressProperty());
            progress.textProperty().bind(task.messageProperty());

            Thread t=new Thread(task);
            t.start();

    }

}





