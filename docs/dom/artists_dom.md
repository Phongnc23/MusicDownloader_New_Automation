# DOM - Module ARTISTS

Screen DOM 1720x2408. Package com.musicdownloaderapp.musicdownloadappfree.mp3download.
Tren thiet bi nay metadata artist deu <unknown> -> chi 1 artist card "<unknown>\nN tracks".

## Artists LIST
- Count "N artists": dung descriptionContains("artists") + regex `(\d+)\s*artists` (descriptionMatches KHONG on dinh).
- Artist card: content-desc "<name>\nN tracks" (descriptionContains(" tracks")).
- Nut 3 cham card (1 artist): tap toa do ~ (770, 1035).
- Nut sort list: (1657, 276).

## Sort dialog (LIST) - bottom sheet
- Title "Sort by" [185,2145][312,2197]. Chi 1 option "Title" [140,2244][1580,2372].
- Option ACTIVE co 1 View con (mui ten chieu) [185,2272][257,2344] -> isSortTitleActive = xpath con.
- Nut X dong: [1463,2135][1535,2207] -> **center (1499, 2171)**.
- Scrim [0,0][1720,2074].
- HANH VI: tap option co the tu dong dong dialog -> check active phai REOPEN (giong Tracks).

## Artist DETAIL
- Back (accessibilityId "Back"), 3 cham detail tap toa do (1666, 109).
- Hero "<name>\nN songs" (descriptionContains(" songs")).
- Section "Albums" (accessibilityId "Albums") + carousel folder; Play all / Shuffle.
- Section "Tracks" + nut sort: artist detail (1657, 926), folder detail (1657, 597).
- Row track: content-desc "<title>\n<artist> • m:ss"; 3 cham track (1666, rowY) -> menu 7 action (giong Tracks).

## Edit sheet 4 action (artist list / detail / folder) - bottom sheet
- Header "<name>\nN songs" + 4 action content-desc: Play / Add to playing queue / Add to playlist / Share track.
- KHONG co Rename / Delete from device / File information.
- Detail sheet DOM: Play [140,1752], Add to playing queue [140,1896], Add to playlist [140,2040], Share track [140,2184]; Scrim [0,0][1720,1510].

## Share business rule: collection >10 bai -> Share BI CHAN (sheet dong, khong mo resolver); <=10 -> mo resolver.

## Select mode (NHAN GIU artist)
- Header: Back/close (Button) [18,73][126,181] -> (72,127). "0 item selected" [144,101]. Select-all (ImageView) [1594,73][1702,181] -> (1648,127).
- Bottom bar: NHAN clickable=false; tap ICON (clickable) o **y=2311** ([.,2275][.,2347]):
  - Add to queue icon [266,2275][338,2347] -> (302, 2311)
  - Add to list icon  [824,2275][896,2347] -> (860, 2311)
  - Share file icon   [1382,2275][1454,2347] -> (1418, 2311)
- Tap artist trong select mode -> toggle chon (count "N item selected" qua descriptionContains).
