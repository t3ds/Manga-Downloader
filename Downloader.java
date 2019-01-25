import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Downloader extends Task<Void>  {

    Download d=new Download();
    static float last_pg=1;
    float cnt=1,incr;
    ChromeDriver driver ;
    int j;
    String chapter_name,manga_name,chapterLink;

    Downloader(String mangaName,String chapter,String chapterHref, int i,ChromeDriver driver1){

       chapter_name=chapter;
       manga_name=mangaName;
       chapterLink=chapterHref;
       driver=driver1;
       j=i;

    }



    private boolean isAttributePresent()
    {
        boolean result=false;
        WebElement flag= driver.findElement(By.cssSelector(".noselect.nodrag.cursor-pointer"));

        try {
            String value = flag.getAttribute("src");
            if (value != null) {
                result = true;
            }
        }
            catch(Exception e)

            {
               //System.out.println(e);
            }

        return result;

    }



    @Override
    protected Void call() throws Exception {

        updateMessage("0%");
        while(cnt!=0) {
            try {
                Files.createDirectories(Paths.get("C:\\Manga\\" + manga_name + "\\" + chapter_name));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(chapter_name);

            if (j > last_pg) {
                cnt = 0;
                break;
            } else {


                driver.get("https://mangadex.org" + chapterLink + "/" + j);
                //WebElement flag= driver.findElement(By.cssSelector(".noselect.nodrag.cursor-pointer"));
                //Thread.sleep(2500);
                // WebDriverWait wait=new WebDriverWait(driver,20);
                //wait.until(ExpectedConditions.attributeToBeNotEmpty(flag,"src"));

                while (isAttributePresent() != true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String pgsrc = driver.getPageSource();

                Document chapterPage = Jsoup.parse(pgsrc);

                Elements last = chapterPage.select("div[data-total-pages]");
                last_pg = d.last_pg = Integer.parseInt(last.last().attr("data-total-pages"));

                Elements sources = chapterPage.select("img[data-chapter]");


                try {
                    URL url1 = new URL(sources.last().attr("src"));
                    InputStream inputStream = url1.openStream();
                    OutputStream outputStream = new FileOutputStream("C:\\Manga\\" + manga_name + "\\" + chapter_name + "\\" + j + ".jpg");

                    byte[] buffer = new byte[2048];
                    int length = 0;

                    System.out.println("Printing page" + j);

                    while ((length = inputStream.read(buffer)) != -1) {

                        outputStream.write(buffer, 0, length);
                    }

                    inputStream.close();
                    outputStream.close();
                } catch (Exception e) {
                    System.out.println("Exception" + e.getMessage());
                }


                incr = j / last_pg;
                int prog= (int) (incr*100);
                //d.pb.progressProperty().bind(d.task.progressProperty());
                //new Thread(d.task).start();
                //Thread t2=new Thread(task);
                //t2.start();

                updateMessage(String.valueOf(prog+"%" + "   "+manga_name+ " "+chapter_name));
                updateProgress(j,last_pg);

                j++;
            }

        }

        return null;
    }
}
