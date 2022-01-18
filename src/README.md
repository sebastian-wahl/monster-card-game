# Monster Trading Cards Game

Monsterkarten Spiel für den Softwareentwicklungsunterricht.

## Card Specialities

- Dark Bat
  - (+) SlownessSpell
- Dark Ent
  - (+) Orc, Knight
- Dragon
  - (+) GreyGoblin
  - (-) FireElf
- FireElf
  - (+) Dragon
- FireWizard
  - (+) Orc
  - (-) WaterWitch
- Grey Goblin
  - (-) Dragon
- Knight
  - (-) WaterSpell, DarkEnt
- Kraken
  - (+) SpellCard
- Orc
  - (-) FireWizard, DarkEnt
- WaterWitch
  - (+) SpellCard, FireWizard
- **All Spells:**
  - **(-) Kraken, WaterWitch**
- SlownessSpell
  - (-) DarkBat
- WaterSpell
  - (+) Knight

# Protokoll

Im folgenden Abschnitt gehe ich ein wenig auf die Implementierung des Spiels ein und die Herausforderungen die sich mir
in dieser Zeit gestellt haben.

Insgesamt habe ich etwas mehr als xx Stunden gebraucht das Spiel zu entwickeln.

## Implementierung

Begonnen habe ich mit der Implementierung/Erstellung der Card Klassen. Dabei habe ich zuerst eine abstrakte Base Klasse
(`CardBase`) erstellt mit den Attributen die bei allen Karten gleich sind. Danach habe ich jeweils für die Arten der
Karten eine Subklasse erstellt, ebenfalls abstrakt. Eine Klasse `SpellCard` und eine Klasse `MonsterCard`. Die
Kampflogik schlängelt sich durch beide Klassen durch. Die Basislogik ist in der `CardBase` Klasse implementiert und bei
gewissen "specialities" (`Knight` drowns when hit with `WaterSpell`, usw...) wurde die `attack` Methode in den
jeweiligen Klassen überschrieben.

Dann habe ich zu dieser `attack` Methode einige Tests geschrieben, um die Funktionalität zu überprüfen.

Anschließend habe ich eine `CardFactory` erstellt die das Instanziierung der Card Objekte übernimmt. Diese Funktion habe
ich in der `Package` Klasse dann verwendet um zufällig in Abhängigkeit von der Seltenheit der Karte (see `CardsEnum`
and `RarityEnum`) 5 Karten zu generieren.

Weiters habe ich alle Grundklasse erstellt wie zum Beispiel `Deck, Stack, Trade` sowie die  `User` Klasse.

Als Nächstes habe ich den Server aufgesetzt. Dazu habe ich eine `Server` klasse erstellt die mittels
eines `ServerSockets` auf neue Verbindungen lauscht. Sobald eine Verbindung etabliert werden konnte habe ich den durch
die Verbindung erstellten "Client" `Socket` an die Worker Klasse `ClientGameRunner` weitergegeben. Das ist notwendig um
eine mehrere Verbindungen mit einem Server gleichzeitig zu erstellen, denn wenn der `Server` sich um die Anfragen des
soeben verbundenen Clients kümmern müsste, könnte er nicht gleichzeitig nach neuen Verbindungen "offen" sein.

Für alle Funktionen bzw. "URL Paths" habe ich einen Controller erstellt. Jeder Controller erbt von der
abstrakten `ControllerBase` Klasse, welche die Methode `doWorkIntern` vorgibt. Im `ClientGameRunner` wird dann der
jeweilige Controller abhängig von dem Path ausgewählt und die `doWork` Methode vom `ControllerBase` aufgerufen. In
dieser Methode wird eine Postgresql Transaction gestartet und nach dem erfolgreichen Ausführen der `doWorkIntern`
Methode (aus der jeweiligen Implementierung) Committed. Im Fall das ein Fehler auftritt und aufgefangen wird, wird ein
Rollback durchgeführt welcher den Stand der Datenbank wieder auf den vor Beginn der Transaction zurücksetzt.

### URL Paths and corresponding Controllers:

- "users" -> `AddAndEditUserController`
- "battles" -> `BattleController`
- "sessions" -> `LoginUserController`
- "packages", "translations" -> `PackageController`
- "cards" -> `StackController`
- "deck" -> `DeckController`
- "stats" -> `UserStatController`
- "score" -> `ScoreboardController`
- "tradings" -> `TradeController`

Dann habe ich eine postgresql Datenbank in einem Docker image aufgesetzt.

## Lessons Learned