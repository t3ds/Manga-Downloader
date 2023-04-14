# Manga-Downloader
A basic manga downloader made using Java and JavaFX


It uses html scraping using selenium and BS4 to scrape mangdex.org

The user is provided with a front-end window where they can type in the name (or part thereof) of a manga. The application will then fetch the names of the manga with that keyword and display the results.

The user can then choose the correct manga from the list. The application fetches the english chapters and displays them.

The user can then choose the chapters they want to download. The chapters will be downloaded into a local folder /[MANGA_NAME]/[CHAPTER NUMBER]/[PAGE_NO].jpg
