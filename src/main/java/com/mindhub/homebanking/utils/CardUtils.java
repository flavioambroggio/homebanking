package com.mindhub.homebanking.utils;

public final class CardUtils {

    private CardUtils() {}

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int contarCifras (int random) {
        int contador = 0;

        while (random !=0) {
            random = random/10;
            contador ++;
        }
        return contador;
    }

    public static String completarCifras (Integer numeroRandom) {
        String numero = Integer.toString(numeroRandom);

        if (contarCifras(numeroRandom) == 3) {
            numero = "0" + numero;
        } else if (contarCifras(numeroRandom) == 2) {
            numero = "00" + numero;
        } else if (contarCifras(numeroRandom) == 1) {
            numero = "000" + numero;
        }
        return numero;

    }


}
