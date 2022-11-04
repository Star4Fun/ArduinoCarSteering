    #include <iostream>

    //Der Preis pro Flasche
    float pBottle = 3.5f;
    //Die Anzahl an Flaschen
    int amount = 0;
    //Der Prozentsatz
    float discount = 0.05f;
    //Mindestmenge um Prozente zu bekommen
    int minAmount = 11;

    void writeHelp() {
        std::cout << "Geben Sie, <+> für eine weiter Flasche, <-> um eine Flasche zu entfernen, <r> um den Vorgang abzuschließen oder <n> zum direkten Anzahl eingeben ein." << std::endl;
    }

    void printCount() {
        std::cout << "Momentan beträgt die Menge an Flaschen: " << amount << " und der Preis " << amount*pBottle << "€" << std::endl;
    }

    int main() {
        //Helfervariable
        bool ready = false;
        //Befehlsübersicht ausgeben
        writeHelp();
        while(!ready) {
            char input = ' ';
            std::cin >> input;
            //Den jeweiligen Input überprüfen
            switch(input) {
                //Anzahl an Flaschen erhöhen und momentane Anzahl ausgeben
                case '+':
                    amount++;
                    printCount();
                break;
                //Anzahl an Flaschen senken und momentane Anzahl ausgeben. Sollten es 0 Flaschen sein wird eine Fehlermeldung ausgegeben.
                case '-':
                    if(amount > 0) {
                        amount--;
                        printCount();
                    } else {
                        std::cout << "Es können nicht weniger Flaschen als 0 sein!" << std::endl;
                    }
                break;
                //Aus der Schleife ausbrechen
                case 'r':
                case 'R':
                    ready = true;
                break;
                //Anzahl an Flaschen direkt eingeben
                case 'n':
                case 'N':   
                    std::cout << "Bitte geben Sie die Anzahl ein" << std::endl;
                    //Anzahl eingeben und bei Fehler die Eingabe wiederholen.
                    do {
                    if(std::cin.fail()) {
                        std::cin.clear();
                        std::cin.ignore(10000, '\n');
                        std::cout << "Falsche Eingabe. Bitte geben Sie eine Zahl ein!" << std::endl;
                    //Prüfen ob die Anzahl positiv ist
                    } else if(amount < 0) {
                        amount = 0;
                        std::cout << "Der Anzahl muss positiv sein!" << std::endl;
                    }
                    std::cin >> amount;
                    } while(std::cin.fail() || amount < 0);
                    ready = true;
                break;
                //Wenn kein Befehl passt Fehlermeldung ausgeben
                default:
                std::cout << "Unbekannter Befehl." << std::endl;
                writeHelp();
                break;
            }
        }
        
        //Gesamtpreis berechnen
        float price = pBottle * amount;
        float toPay = price;
        
        //Wenn mehr als die Mindestmenge gekauft wurde den Prozentsatz abziehen
        if(amount >= minAmount) {
            toPay = toPay * (1.0f-discount);
        }
        
        //Kosten des Kundens ausgeben
        std::cout << "Der Kunde muss " << toPay << "€ bezahlen. Er hat " << amount << " Flaschen gekauft." << std::endl;
        
        //Wenn der Kunde einen Rabatt bekommen hat dies ausgeben
        if(toPay != price) {
            std::cout << "Der Kunde hat durch den Rabatt " << (price - toPay) << "€ gespart!" << std::endl;
        }
        
        //Programmende
        return 0;
    }
