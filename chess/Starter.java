package chess;

import chess.viewmodel.FileInputHandler;

import java.io.File;

/**
 * Created by Stephen on 5/9/2014.
 */
public class Starter {

    public static void main(String[] args) {
        if(args.length == 1) {
            FileInputHandler fIH = new FileInputHandler();
            fIH.executeFromFile(new File(args[0]));
        }
    }
}
