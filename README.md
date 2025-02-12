Dokumentation - Notizen zum Projekt
06.11.2024
Git
Immer Freitags: alle Branches in einen Branch mergen (master, staging, develop, o.ä.).
Auf den Feature-Branches kann trotzdem normal weiter gearbeitet werden, Pull Requests können trotzdem offen bleiben, etc.
Tests
Mocks für die DB-Verbindung nicht notwendig bzw. auch nicht sinnvoll (mehr Arbeit und weniger aussagekräftig). Ihr könnt davon ausgehen, dass ein MariaDB-Server verfügbar ist.


Interfaces von Datenhaltungsklassen implementiert
![image](https://github.com/user-attachments/assets/be063961-792b-4444-9929-7b090abac106)

Datenbankverbindung
![image](https://github.com/user-attachments/assets/cdc66c73-3d6a-46a7-bd16-c153fe9ac45d)

----------------------------------------------------------------------------------------------------------------------------------

<h1>Offizielle Dokumentation vom Projekt "HausFix"</h1>
<strong>Durchgeführt von Barbara, Denise, Armando und Dustin</strong>

Durchführungszeitraum: 7.10.2024 - 23.05.2025

<h2>Projektbeschreibung:   </h2>
Entwicklung einer Client-Server-Anwendung, die Ablesedaten für Strom, Heizung, Warmwasser und weitere Verbrauchsdaten pro Kunden effizient verwaltet.   

<h2>Projektschritte:</h2>
1. Einrichtung der Entwicklungsumgebung (Installierung der notwendigen Werkzeuge: OpenJDK 21, Eclipse EE 2024-06 oder Alternative, MariaDB 11.3, Maven 3.9, JUnit 5.10.3)   <br>
1.1 Einrichten GIT (GitHub)  
   
2. Datenbank aufsetzen (Sprint 1)  
2.1 DB nach Vorgabe erstellen  
2.2 Anbindung mittels Java-JDBC  
2.3 JUnit-Tests entwickeln  
   
3. REST-Server aufsetzen (Sprint 2)  
3.1 Entwicklung der grundlegenden Server-Komponente in Java, die eine REST-API bereitstellt und einfache Datenbankabfragen ermöglicht  
3.2 Umsetzung der REST-Schnittstellendefinition  
3.3 Implementierung der grundlegenden Java-Client-Komponente, die Anfragen an den Server stellt und die Ergebnisse darstellt und mittels JUnit testet  
3.4 Datenschutz und Datensicherheit analysieren  
   
4. GUI mit Import/Export (Sprint 3)  
4.1 Erstellen eines GUI-Clients für die Verwaltung der Daten  
4.2 Geeignete Programmiersprache für GUI-Framework auswählen  
4.3 Import & Export (JSON, XML, CSV) implementieren  
   
5. Erweiterung (Sprint 4)  
5.1 Umsetzung einer Erweiterung: Userverwaltung mit Authentifizierung, Grafische Auswertung der Messdaten, Konsolenclient: Import & Export über REST Server, REST über XML mit XSD/DTD-Datei, …  


<h2>Projektziel:</h2>
• Erfassen und Verwalten von Ablesedaten:    
Die Anwendung soll Ablesedaten für verschiedene Verbrauchszähler (Strom, Heizung, Warmwas-ser etc.) speichern und verwalten<br><br>

• Client-Server-Architektur:    
Die Anwendung wird als Client-Server-System realisiert, wobei der Client Anfragen an den Server stellt und der Server diese bearbeitet und die entsprechenden Daten liefert oder speichert

• Verwendung von REST:     
Für die Kommunikation zwischen Client und Server wird das REST-Protokoll verwendet

• Datenbankintegration:    
Die Daten werden in einer MariaDB-Datenbank gespeichert, auf die mittels JDBC zugegriffen wird

<br>• Testen mit JUnit:   
Alle Funktionalitäten sollen mit JUnit-Tests überprüft werden, um eine hohe Softwarequalität sicherzustellen

<h2> Entwicklungsprozess </h2>
<h3>1. Einrichten der Entwicklungsumgebung</h3>
Es wurde sich nach Absprache im Team für die Entwicklungsumgebung IntelliJ Community Edition entschieden und diese wird fortlaufend für das Projekt verwendet.

<h3>1.1 Einrichten GIT (GitHub)</h3>
Des weiteren wurde GIT bzw. GitHub eingerichtet, ein Repository erstellt, ein Kanban-Board angelegt und die Aufgaben so gerecht verteilt.

<h3>2. Datenbank erstellen</h3>
Es wurde bei jedem Teammitglied eine Version von MariaDB lokal auf den jeweiligen Geräten installiert.

<h3>2.1 DB nach Vorgabe erstellen</h3>
Die Datenbank wurde nach folgendem Schema implementiert: 
![Bildschirmfoto vom 2025-02-12 15-17-19](https://github.com/user-attachments/assets/e004cb8b-ea03-4278-8a6a-e3631aa22adc)

