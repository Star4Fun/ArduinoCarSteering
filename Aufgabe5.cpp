    #include <iostream>

    int main() {
        int x; 
        do { 
            std::cin >> x;
            if(std::cin.fail()) {               //Prüfen ob Cin fehlgeschlagen ist
                std::cout << "Not a vaild integer!" << std::endl;
                std::cin.clear();               //Den Fehler im Stream löschen
                std::cin.ignore(10000, '\n');   //Den Rest der Line ignorieren
            }
        } 
        while (x != 1); 
        return 0;
    }

