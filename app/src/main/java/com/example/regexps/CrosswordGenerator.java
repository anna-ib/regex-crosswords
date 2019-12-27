package com.example.regexps;

import java.util.Random;

class CrosswordGenerator {

    private final Random random = new Random();
    private final String alphabet = "abcdefghijklmnopqrsyuvwxyzABCDEFGHIJKLMNOPQRSYUVWXYZ0123456789";

    private int width = 3;
    private int height = 3;
    private String[] crossword = new String[height];
    private String[] reCrosswordHorizontal = new String[height];
    private String[] reCrosswordVertical = new String[width];

    private StringBuilder reg = new StringBuilder();
    private int curLength = 0;

    private interface Generator {
        void generate(String str);
    }

    private Generator[] generators = new Generator[] {
            new Generator() { public void generate(String str) { generateFakeCharAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateCharAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateString(str); }},
            //new Generator() { public void generate(String str) { generateGroupAndQuantifier(str); }},
            //new Generator() { public void generate(String str) { generateIntervalAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateAlternation(str); }}
    };

    CrosswordGenerator(int width, int height) {
        if (width > 0) this.width = width;
        if (height > 0) this.height = height;
    }

    String[] getCrosswordHorizontal() {
        return reCrosswordHorizontal;
    }

    String[] getCrosswordVertical() {
        return reCrosswordVertical;
    }

    private String generateRe(String str) {
        this.reg = new StringBuilder();
        while (this.curLength < str.length()){
            final int rand = random.nextInt(str.length() - this.curLength);
            generators[random.nextInt(generators.length)].generate(str.substring(this.curLength, this.curLength + rand + 1));
            this.curLength += rand + 1;
        }
        this.curLength = 0;
        return this.reg.toString();
    }

    private void generateString(String str) {
        this.reg.append(str);
    }

    private void generateGroupAndQuantifier(String str) {
        generateGroup(str);
        generateQuantifier(str.length());
    }

    private void generateIntervalAndQuantifier(String str) {
        generateInterval(str);
        generateQuantifier(str.length());
    }

    private void generateFakeCharAndQuantifier(String str) {
        this.reg.append(this.alphabet.charAt(random.nextInt(61)));
        generateQuantifier(0);
        generators[random.nextInt(generators.length - 1) + 1].generate(str);
    }

    private void generateCharAndQuantifier(String str) {
        this.reg.append(".");
        generateQuantifier(str.length());
    }

    private void generateAlternation(String str) {
        this.reg.append("(").append(str);
        for (int i = 0; i < random.nextInt(2) + 1; i++) this.reg.append("|").append(generateWord(str.length()));
        this.reg.append(")");
    }

    private void generateGroup(String str) {}

    private void generateInterval(String str) {}

    private void generateQuantifier(int count) {
        final String[] quantifiers = {"", "+", "*", "{" + count + "}", "{" + (count - random.nextInt(count + 1)) + ",}", "{" + (count - random.nextInt(count + 1)) + "," + (count + random.nextInt(count + 1)) + "}", "?"};
        switch (count) {
            case 0: { this.reg.append(quantifiers[random.nextInt(quantifiers.length - 2) + 2]); break; }
            case 1: { this.reg.append(quantifiers[random.nextInt(quantifiers.length)]); break; }
            default: { this.reg.append(quantifiers[random.nextInt(quantifiers.length - 2) + 1]); break; }
        }
    }

    void createCrossword() {
        for (int i = 0; i < this.height; i++) this.crossword[i] = generateWord(this.width);
        fillReCrossword();
    }

    private void fillReCrossword() {
        for (int i = 0; i < this.height; i++) this.reCrosswordHorizontal[i] = generateRe(this.crossword[i]);
        for (int i = 0; i < this.width; i++){
            final StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.height; j++) str.append(this.crossword[j].charAt(i));
            this.reCrosswordVertical[i] = generateRe(str.toString());
        }
    }

    private String generateWord(int len) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++){
            str.append(this.alphabet.charAt(random.nextInt(61)));
        }
        return str.toString();
    }
}
