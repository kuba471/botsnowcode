# BotSnow AutoAH Mod (Fabric, 1.21.4)

Duży, rozbudowany mod klientowy do AH:
- keybind do GUI,
- keybind do start/stop AutoAH,
- zaawansowane reguły kupna,
- ultra szybkie odświeżanie (`iron_axe`) + potwierdzanie (`lime_dye`),
- historia ostatnich 5 zakupów,
- webhook po zakupie,
- auto reconnect na `anarchia.gg`,
- auto `/login <hasło>` po wejściu.

## Najważniejsze elementy
- **Reguły**: nazwa, item id, lore, enchanty, max cena, aktywna/wyłączona.
- **GUI**: kolorowe panele, sekcja reguł, sekcja ustawień globalnych, sekcja historii zakupów.
- **Middle click**: przypisanie itemu do reguły z main hand.
- **Reconnect/Login**: osobne przełączniki i konfiguracja.

## Uwaga
To nadal klientowy mod automatyzujący interakcje GUI. Dla stabilności i bezpieczeństwa dopracuj timingi pod konkretny serwer.

## Mega rozbudowa architektury
- Dodano rozbudowane moduły: `loop/`, `webhook/`, `widget/`, `config/`, `util/`.
- Łącznie projekt ma teraz dużo więcej plików (ponad 50 plików Java) dla łatwiejszego dalszego rozwoju.
- Pętla AutoAH dostała dodatkowe statystyki i historię akcji dla szybkiego debugowania.

## Szybki start
- Uruchom `start.bat`, żeby od razu pobrać zależności i odpalić build (`--refresh-dependencies`).
