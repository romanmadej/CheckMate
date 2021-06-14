# CheckMate

Projekt na kurs Programowanie Obiektowe 2020/2021.

## Uruchamianie

Plik jar aplikacji można pobrać w zakładce *Releases*. Aplikacja wykorzystuje platformę JavaFX.

Ze strony [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) w sekcji *Latest release* można
aktualnie pobrać JavaFX SDK w wersji 16. Do uruchomienia aplikacji będziemy potrzebowali ścieżkę do folderu lib z
pobranej paczki.

W celu uruchomienia aplikacji należy wykonać poniższe polecenie, zamieniając `PATH_TO_JAVAFX_SDK` na odpowiednią
ścieżkę.

```
java --module-path PATH_TO_JAVAFX_SDK/lib --add-modules=javafx.controls,javafx.fxml -jar Checkmate.jar
```

## Zrzuty ekranu

![zrzut ekranu 1](./Screenshots/screenshot1.png "Zrzut ekranu 1")

![zrzut ekranu 2](./Screenshots/screenshot2.png "Zrzut ekranu 2")
