# Monster Trading Cards Game

Monsterkarten Spiel für den Softwareentwicklungsunterricht.

## Card Specialities

- Dark Bat
  - (+) SlownessSpell
- Dark Ent
  - (+) Ork, Knight
- Dragon
  - (+) GreyGoblin
  - (-) FireElf
- FireElf
  - (+) Dragon
- FireWizard
  - (+) Ork
  - (-) WaterWitch
- Grey Goblin
  - (-) Dragon
- Knight
  - (-) WaterSpell, DarkEnt
- Kraken
  - (+) SpellCard
- Ork
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
Kampflogik schlängelt sich durch alle Klassen durch. Die Basislogik ist in der `CardBase` Klasse implementiert und bei
gewissen "specialities" (`Knight` drowns when hit with `WaterSpell`, usw...) wurde die `attack` Methode in den
jeweiligen Klassen überschrieben.

Dann habe ich zu dieser `attack` Methode einige Tests geschrieben, um die Funktionalität zu überprüfen.

Anschließend habe ich eine `CardFactory` erstellt die das Instanziierung der Card Objekte übernimmt. Diese Funktion habe
ich in der `Package` Klasse dann verwendet um zufällig in Abhängigkeit von der Seltenheit der Karte (see `CardsEnum`
and `RarityEnum`) 5 Karten zu generieren.

## Lessons Learned