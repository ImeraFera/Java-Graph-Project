import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Graf {

    ArrayList<Sehir> sehirler = new ArrayList<>();
    ArrayList<Kisi> kisiler = new ArrayList<>();
    int mesafeler[][];

    public Graf() throws FileNotFoundException {
        dosyaOku();
    }

    public void dosyaOku() throws FileNotFoundException {
        try (Scanner scan = new Scanner(new File("Bilgiler2.txt"))) {
            String line = scan.nextLine();

            // * Şehirleri oku
            while (!line.equals("Graf:")) {

                line = scan.nextLine();
                if (line.equals("Graf:")) {
                    break;
                }

                Sehir sehir = new Sehir(line.split(" ")[0], line.split(" ")[1]);
                sehirler.add(sehir);

            }
            mesafeler = new int[sehirler.size()][sehirler.size()];

            line = scan.nextLine();
            int i = 0;
            while (!line.equals("Kişiler ve Başlangıç Noktaları:")) {

                line = scan.nextLine();

                if (line.equals("Kişiler ve Başlangıç Noktaları:")) {
                    break;
                }

                String lineArr[] = line.substring(2).split("\t");

                for (int k = 0; k < mesafeler.length; k++) {
                    mesafeler[i][k] = Integer.valueOf(lineArr[k]);
                }
                i++;

            }

            while (!line.equals("Kişilerin Seyahat Noktaları ve Hızları:")) {
                line = scan.nextLine();
            }

            while (scan.hasNextLine()) {
                line = scan.nextLine();
                ArrayList<Sehir> rotaList = new ArrayList<>();
                String[] rotaArr = line.split(" ")[1].split("-");
                for (int j = 0; j < rotaArr.length; j++) {

                    for (int j2 = 0; j2 < sehirler.size(); j2++) {

                        if (sehirler.get(j2).sehirAdi.equals(rotaArr[j])) {
                            rotaList.add(sehirler.get(j2));
                        }
                    }

                }

                Kisi kisi = new Kisi(line.split(" ")[0], rotaList,
                        Double.parseDouble(line.split(" ")[2].replaceAll(",", ".")));
                kisiler.add(kisi);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void run() throws FileNotFoundException {
        try (Scanner scan = new Scanner(System.in)) {

            System.out.println("Varış süresi hesaplanacak olan kişinin ismini giriniz:");
            String kisiAdi = scan.nextLine();
            varisSuresiHesapla(kisiAdi);

            System.out.println("Toplam yol uzunluğu hesaplanacak olan kişinin ismini giriniz:");
            String kisiAdi2 = scan.nextLine();
            toplamYolUzunluguHesapla(kisiAdi2);

            System.out.println(
                    "İki şehir arasında direkt yol olup olmadığını sorgulamak için şehir isimlerini aralarında bir boşluk bırakarak giriniz:");
            String yol = scan.nextLine();
            direktYolVarmi(yol);

            System.out.println(
                    "Kimin hangi saat itibariyle hangi noktada olduğunu hesaplamak için, aralarında bir boşluk bırakarak kişi ismini ve saati giriniz:");
            String kisiVeSaat = scan.nextLine();
            kimNerede(kisiVeSaat);
        }

    }

    public void kimNerede(String kisiVeSaat) {
        String kisiAd = kisiVeSaat.split(" ")[0];
        String saat = kisiVeSaat.split(" ")[1];
        Kisi hedefKisi = new Kisi(null, null, 0);
        for (Kisi kisi : kisiler) {

            if (kisi.kisiAd.equals(kisiAd)) {
                hedefKisi = kisi;
            }

        }

        saat = saat.substring(0, 2);
        if (Integer.parseInt(saat) <= 8) {

            System.out.println(hedefKisi.kisiRota.get(0).sehirAdi);
        } else if (Integer.parseInt(saat) > 8 && Integer.parseInt(saat) <= 24) {

            int saatFarki = Math.abs(8 - Integer.parseInt(saat));

            Double gidilenMesafe = saatFarki * 60 * hedefKisi.kisiHiz;

            double toplamMesafe = 0;
            for (int i = 1; i < hedefKisi.kisiRota.size(); i++) {
                int j = i - 1;
                toplamMesafe += mesafeler[Integer.parseInt(hedefKisi.kisiRota.get(j).sehirNo) - 1][Integer
                        .parseInt(hedefKisi.kisiRota.get(i).sehirNo) - 1];
            }

            if (gidilenMesafe >= toplamMesafe) {
                gidilenMesafe = toplamMesafe;
            }

            for (int i = 1; i < hedefKisi.kisiRota.size(); i++) {
                int j = i - 1;

                if (mesafeler[j][i] <= gidilenMesafe) {
                    gidilenMesafe -= mesafeler[j][i];
                    continue;
                } else {
                    System.out.println(hedefKisi.kisiRota.get(j).sehirAdi + " " + hedefKisi.kisiRota.get(i).sehirAdi
                            + " arasindadir. ");
                    break;
                }

            }

        }

    }

    public void varisSuresiHesapla(String kisiAd) {
        Kisi hedefKisi = new Kisi(null, null, 0);

        for (Kisi kisi : kisiler) {
            if (kisi.kisiAd.equals(kisiAd)) {
                hedefKisi = kisi;
            }
        }
        double kisiSaattekiHizi = hedefKisi.kisiHiz;
        int gidilenMesafe = 0;
        for (int i = 1; i < hedefKisi.kisiRota.size(); i++) {
            int j = i - 1;
            gidilenMesafe += mesafeler[Integer.parseInt(hedefKisi.kisiRota.get(j).sehirNo) - 1][Integer
                    .parseInt(hedefKisi.kisiRota.get(i).sehirNo) - 1];
        }
        double kisiVarisSuresi = gidilenMesafe / kisiSaattekiHizi;
        System.out.println(hedefKisi.kisiAd + " " + hedefKisi.kisiRota.get(hedefKisi.kisiRota.size() - 1).sehirAdi
                + " varis suresi " + kisiVarisSuresi + " dk");
    }

    public void toplamYolUzunluguHesapla(String kisiAd) {
        Kisi hedefKisi = new Kisi(null, null, 0);

        for (Kisi kisi : kisiler) {
            if (kisi.kisiAd.equals(kisiAd)) {
                hedefKisi = kisi;
            }
        }

        int gidilenMesafe = 0;
        for (int i = 1; i < hedefKisi.kisiRota.size(); i++) {
            int j = i - 1;
            gidilenMesafe += mesafeler[Integer.parseInt(hedefKisi.kisiRota.get(j).sehirNo) - 1][Integer
                    .parseInt(hedefKisi.kisiRota.get(i).sehirNo) - 1];
        }
        System.out.println(hedefKisi.kisiAd + " toplam mesafesi " + gidilenMesafe + " km");
    }

    public void direktYolVarmi(String yol) {

        String sehir1Adi = yol.split(" ")[0];
        String sehir2Adi = yol.split(" ")[1];
        String sehir1No = "";
        String sehir2No = "";

        for (Sehir sehir : sehirler) {
            if (sehir1Adi.equals(sehir.sehirAdi)) {
                sehir1No = sehir.sehirNo;
            }
            if (sehir2Adi.equals(sehir.sehirAdi)) {
                sehir2No = sehir.sehirNo;

            }
        }

        if (mesafeler[Integer.parseInt(sehir1No) - 1][Integer.parseInt(sehir2No) - 1] != 0) {
            System.out.println(sehir1Adi + " ile " + sehir2Adi + " arasında direkt yol var");
        } else {
            System.out.println(sehir1Adi + " ile " + sehir2Adi + " arasında direkt yol yok");

        }
    }

}
