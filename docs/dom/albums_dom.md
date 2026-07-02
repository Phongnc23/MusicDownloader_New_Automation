# DOM - Module ALBUMS

Screen 1720x2408. Package com.musicdownloaderapp.musicdownloadappfree.mp3download.
Moi "album" thuc chat la 1 folder. Grid 2 cot. Tren thiet bi: 5 albums
(VoiceChanger 321, RecoveredAudios 1, Notifications 1, Music Download 3, BrowserDownloader 3).

## Albums LIST (grid 2 cot)
- Title "Albums" (content-desc) [0,0][1720,231].
- Count "N albums" [36,254][165,297] -> dung descriptionContains("albums") + regex `(\d+)\s*albums`
  (descriptionMatches KHONG on dinh; tab "Albums" + title "Albums" khong co so nen regex bo qua).
- Nut sort list: Button [1612,231][1702,321] -> **center (1657, 276)**.
- Album card: content-desc "<name>\nN tracks" (descriptionContains(" tracks")), View clickable + long-clickable.
  - Card LUOI 2 cot, moi card cao ~1060px. col1 x[0..860], col2 x[860..1720].
  - Card col1-row1 (VoiceChanger) [0,330][860,1390]; nut 3 cham con Button [716,981][824,1089]
    -> **center (770, 1035)** = ALBUM_CARD_MENU (3 cham card dau col1).
  - Card col2 nut 3 cham ~ center (1630, 1035).
- Bottom nav: Home[9..349], Tracks[349..690], Artists[690..1030], Albums[1030..1371], Playlists[1371..1711] (y 2262..2408).
- Viewport chi render ~4 card (card thu 5 phai scroll) -> count(5) >= card render(4).
- LUU Y: Flutter ScrollView AO HOA card ngoai viewport (card thu 5 KHONG co trong tree luc dau).
  UiScrollable.scrollIntoView KHONG on dinh tren Flutter -> mo album theo ten phai SWIPE THU CONG
  (swipe vung luoi 860,1900 -> 860,800) lap toi khi existsImmediately thay card, roi click.

## Album DETAIL (folder-style, chi Tracks - KHONG co section Albums)
- Back (accessibilityId "Back") [9,64][117,154] -> (63,109).
- 3 cham detail: Button [1612,64][1720,154] -> **center (1666, 109)**.
- Hero "<name>\nN songs" (descriptionContains(" songs")) [0,0][1720,440].
- Play all [36,458][847,548] (desc "Play all"); Shuffle [874,458][1684,548] (desc "Shuffle").
- "Tracks" label [36,576][131,619]; nut sort section Tracks: Button [1612,552][1702,642] -> **center (1657, 597)**.
- Row track: ImageView content-desc "<title>\n<unknown> • m:ss", co " • "; 3 cham track Button x[1612..1720] -> center 1666, rowY.
- In-album sort = dialog 7 option GIONG man Tracks: X dong [1463,1365][1535,1437] -> (1499, 1401).
  HANH VI: chon 1 option -> ap dung + DONG dialog ngay -> doc indicator phai REOPEN.

## Album-level sort dialog (LIST, mo tu nut 1657,276)
- Chi 1 option "Title" (giong Artists). X dong ~ (1499, 2171). Tap option -> auto-close -> check active phai reopen.

## Edit sheet 4 action (album-level, mo tu 3 cham card HOAC 3 cham detail)
- Play / Add to playing queue / Add to playlist / Share track (accessibilityId). Header "<name>\nN songs".
- KHONG co Rename / Delete / File information.
- Share business rule: album >10 bai -> Share BI CHAN (sheet dong, khong resolver); <=10 -> mo resolver.
  (Tren thiet bi nay chi VoiceChanger 321 > 10.)

## Select mode (NHAN GIU album) - CO HO TRO
- Close/back: Button [18,73][126,181] -> (72,127). "0 item selected" [144,101][1576,153] (descriptionContains).
- Select-all: ImageView [1594,73][1702,181] -> (1648,127).
- Card trong select mode van content-desc "<name>\nN tracks" -> tap toggle chon.
- Bottom bar (ICON clickable o y=2311, NHAN clickable=false o y~2373):
  - Add to queue icon [266,2275][338,2347] -> (302, 2311); label "Add to queue" [225,2356][379,2390]
  - Add to list  icon [824,2275][896,2347] -> (860, 2311); label "Add to list" [801,2356][919,2390]
  - Share file   icon [1382,2275][1454,2347] -> (1418, 2311); label "Share file" [1363,2356][1473,2390]

## Track 3 cham (trong album detail) = sheet 7 action GIONG man Tracks -> dung TracksPage de assert.
