package com.example.regexps;

import java.util.Random;

class CrosswordGenerator {

    private final Random random = new Random();
    private final String alphabet = "abcdefghijklmnopqrsyuvwxyzABCDEFGHIJKLMNOPQRSYUVWXYZ0123456789";

    private int width;
    private int height;
    private String[] crossword;
    private String[] reCrosswordHorizontal;
    private String[] reCrosswordVertical;

    private StringBuilder reg = new StringBuilder();
    private int curLength = 0;

    CrosswordGenerator(int width, int height) {
        if (width > 0) this.width = width;
        if (height > 0) this.height = height;
    }

    private interface Generator {
        void generate(String str);
    }

    private Generator[] generators = new Generator[] {
            new Generator() { public void generate(String str) { generateFakeCharAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateCharAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateString(str); }},
            new Generator() { public void generate(String str) { generateGroupAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateIntervalAndQuantifier(str); }},
            new Generator() { public void generate(String str) { generateAlternation(str); }}
    };

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
        char a;
        boolean negate = random.nextBoolean();
        if (negate) {
            this.reg.append("[^");
            for (int i = 0; i < random.nextInt(4) + 1; i++) {
                a = this.alphabet.charAt(random.nextInt(62));
                if (str.indexOf(a) == -1) this.reg.append(a);
                else i--;
            }
        }
        else {
            StringBuilder string = new StringBuilder(str);
            int index;
            this.reg.append("[");
            for (int i = 0; i < str.length(); i++) {
                index = random.nextInt(string.length());
                this.reg.append(string.charAt(index));
                string.deleteCharAt(index);
                if (random.nextBoolean()) {
                    a = this.alphabet.charAt(random.nextInt(62));
                    if (str.indexOf(a) == -1) this.reg.append(a);
                }
            }
        }
        this.reg.append("]");

        generateQuantifier(str.length());
    }

    private void generateIntervalAndQuantifier(String str) {
        int[] charIndexes = new int[str.length()];
        for (int i = 0; i < str.length(); i++) {
            charIndexes[i] = this.alphabet.indexOf(str.charAt(i));
            for (int j = i; j > 0; j--) {
                if (charIndexes[j] < charIndexes[j-1]) {
                    charIndexes[j] += charIndexes[j-1];
                    charIndexes[j-1] = charIndexes[j] - charIndexes[j-1];
                    charIndexes[j] -= charIndexes[j-1];
                }
                else break;
            }
        }
        int maxCharIndex = -1, minCharIndex = -1;
        boolean negate = random.nextBoolean();
        if (negate) {
             this.reg.append("[^");
             int index = random.nextInt(str.length() + 1);

             while(minCharIndex == maxCharIndex || maxCharIndex - minCharIndex == 1) {
                 if (index == 0) {
                     minCharIndex = -1;
                     maxCharIndex = charIndexes[0];
                 } else if (index == str.length()) {
                     minCharIndex = charIndexes[index - 1];
                     maxCharIndex = 62;
                 } else {
                     minCharIndex = charIndexes[index - 1];
                     maxCharIndex = charIndexes[index];
                 }
                 if (++index > str.length()) index = 0;
             }

             minCharIndex++;
             maxCharIndex--;
        }
        else {
            this.reg.append("[");
            minCharIndex = charIndexes[0];
            maxCharIndex = charIndexes[str.length() - 1];
            minCharIndex = minCharIndex - random.nextInt(minCharIndex + 1);
            maxCharIndex = maxCharIndex + random.nextInt(62 - maxCharIndex);
        }

        this.reg.append(this.alphabet.charAt(minCharIndex));
        if (minCharIndex != maxCharIndex) {
            if (minCharIndex < 25) {
                this.reg.append("-");
                if (maxCharIndex < 26) this.reg.append(this.alphabet.charAt(maxCharIndex));
                else if (maxCharIndex == 26) this.reg.append("zA");
                else if (maxCharIndex < 52) this.reg.append("zA-").append(this.alphabet.charAt(maxCharIndex));
                else if (maxCharIndex == 52) this.reg.append("zA-Z_0");
                else this.reg.append("zA-Z_0-").append(this.alphabet.charAt(maxCharIndex));
            }
            else if (minCharIndex == 25) {
                if (maxCharIndex == 26) this.reg.append("A");
                if (maxCharIndex < 52) this.reg.append("A-").append(this.alphabet.charAt(maxCharIndex));
                else if (maxCharIndex == 52) this.reg.append("A-Z_0");
                else this.reg.append("A-Z_0-").append(this.alphabet.charAt(maxCharIndex));
            }
            else if (minCharIndex < 51) {
                this.reg.append("-");
                if (maxCharIndex < 52) this.reg.append(this.alphabet.charAt(maxCharIndex));
                else if (maxCharIndex == 52) this.reg.append("Z_0");
                else this.reg.append("Z_0-").append(this.alphabet.charAt(maxCharIndex));
            }
            else if (minCharIndex == 51) {
                if (maxCharIndex == 52) this.reg.append("_0");
                else this.reg.append("_0-").append(this.alphabet.charAt(maxCharIndex));
            }
            else this.reg.append("-").append(this.alphabet.charAt(maxCharIndex));
        }

        this.reg.append("]");
        generateQuantifier(str.length());
    }

    private void generateFakeCharAndQuantifier(String str) {
        this.reg.append(this.alphabet.charAt(random.nextInt(62)));
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

    private void generateQuantifier(int count) {
        final String[] quantifiers = {"", "+", "{" + count + "}", "{" + (count - random.nextInt(count + 1)) + "," + (count + random.nextInt(count + 1) + 1) + "}", "*", "{" + (count - random.nextInt(count + 1)) + ",}", "?"};
        switch (count) {
            case 0: { this.reg.append(quantifiers[random.nextInt(quantifiers.length - 4) + 4]); break; }
            case 1: { this.reg.append(quantifiers[random.nextInt(quantifiers.length)]); break; }
            default: { this.reg.append(quantifiers[random.nextInt(quantifiers.length - 2) + 1]); break; }
        }
    }

    void createCrossword() {
        this.crossword = new String[this.height];
        for (int i = 0; i < this.height; i++) this.crossword[i] = generateWord(this.width);
        fillReCrossword();
    }

    private void fillReCrossword() {
        this.reCrosswordVertical = new String[this.height];
        for (int i = 0; i < this.height; i++) this.reCrosswordVertical[i] = generateRe(this.crossword[i]);

        this.reCrosswordHorizontal = new String[this.width];
        for (int i = 0; i < this.width; i++){
            final StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.height; j++) str.append(this.crossword[j].charAt(i));
            this.reCrosswordHorizontal[i] = generateRe(str.toString());
        }
    }

    private String generateWord(int len) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++){
            str.append(this.alphabet.charAt(random.nextInt(62)));
        }
        return str.toString();
    }

    String[] getCrosswordHorizontal() { return reCrosswordHorizontal; }

    String[] getCrosswordVertical() { return reCrosswordVertical; }

    int getWidth() { return this.width; }

    int getHeight() { return this.height; }

}
