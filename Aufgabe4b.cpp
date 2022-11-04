    #include <iostream>

    
    int main() {
        bool ready = false;
        
        int number = 0;
        
        std::cout << "Geben Sie eine Nummer ein. Diese wird gefiltert und nur 1 oder 2 kommen durch. Um das Programm zu beenden, tätigen Sie eine Eingabe, die keine Zahl ist." << std::endl;
        while(!ready){
            std::cin >> number;
            //Prüfen ob die Nummer in den Filter passt
            if(number != 1 && number != 2) {
                std::cout << "Die Eingabe ist ungültig! Sie muss 1 oder 2 sein!" << std::endl;
            }
            //Cin auf Fehler überprüfen. Wenn einer auftritt bedeutet dies, das keine Zahl eigegeben wurde und das Programm beendet werden soll
            if(std::cin.fail()) {
                ready = true;
            }
        }
        
        return 0;
    }
