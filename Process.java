import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static org.jsoup.Jsoup.connect;

public class Process {


//Defining the URL
    URL url;

    {
        try {
            url = new URL("https://mangadex.org");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    String mangaName, chapter;
    int last_pg = 1, optionSize,length=1;
    Document doc;
    ArrayList<String> chapterHref = new ArrayList<>();
    ArrayList<String> chapterNames;




    /*String getName() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter The Name Of The Manga: ");
        String name = sc.nextLine();

        return name;
    }*/

//Gets the list of relevant Manga
    String[] listOptions(String name) throws IOException {

        String nameUrl = name.replaceAll(" ", "%20");

            doc = connect(url + "/quick_search/" + nameUrl).get();
        Elements options = doc.getElementsByClass("ml-1 manga_title text-truncate");
        optionSize = options.size();
        String opt[] = new String[options.size()];
        int i = 0;
        for (Element option : options) {
            opt[i] = option.text();
            //System.out.println((i+1)+ ". " + opt[i]);
            i++;
        }
        return opt;
    }


//Gets The List Of Chapters

    ArrayList<String> getChapters(String mangaName) throws IOException {

       chapterNames = new ArrayList<>();

        Elements links = doc.select("a[title="+mangaName+"][href]");
        String linkHref = null;

        linkHref=links.first().attr("href");
        //System.out.println(linkHref);

        Document mangaUrl = connect(url + linkHref).get();
        Elements chapters=mangaUrl.select("a.text-truncate");
        Elements pages = mangaUrl.select("a.page-link");
        //String last=pages.last().attr("href");

        System.out.println(length);
        //String chapterNames[]=new String[chapters.size()];
        int i=0,j=0,flag=1;
        do
        {
            i++;
            /*if ((url + linkHref + "/chapters/" + i + "/").equals(url+pages.last().attr("href"))==true)
            {
                length=0;
            }*/


            Document next_pg = connect(url + linkHref + "/chapters/" + i + "/").get();
            chapters = next_pg.select("a.text-truncate");

            for (Element chapter : chapters)
            {
                chapterNames.add(chapter.text());
                chapterHref.add(chapter.attr("href"));
                //System.out.println(chapterNames.get(j));
                j++;
            }



        }while((url + linkHref + "/chapters/" + i + "/").equals(url+pages.last().attr("href"))==false);


        return chapterNames;


    }
    
}
