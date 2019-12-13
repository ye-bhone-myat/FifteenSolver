package edu.oswego.csc375;

public class App {
    public static void main(String[] args){
        if (args.length < 1){
            UISolver.main(null);
        } else {
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("terminal") || args[0].equalsIgnoreCase("t")) {
                    TerminalSolver.main(new String[]{args[1], args[2], args[3]});
                }
            }
        }
    }
}
