# DOM - Man TRACKS (list bai da tai)

Screen size DOM: 1720 x 2364 (status bar an), app full 1720 x 2408.
Package: com.musicdownloaderapp.musicdownloadappfree.mp3download

## Header (y 64..244)
| Element | content-desc | class | bounds | tap center |
|---|---|---|---|---|
| Drawer icon | (khong) | ImageView clickable | [9,64][117,154] | 63,109 |
| Title | `Tracks` | View | [126,83][245,135] | - |
| Search icon | (khong) | ImageView clickable | [1594,64][1702,154] | 1648,109 |
| Count | `369 tracks` | View | [36,178][188,220] | - |
| SORT | (khong content-desc) | Button clickable | [1612,154][1702,244] | **1657,199** |
| Play all | `Play all` | View clickable | [36,244][847,334] | 441,289 |
| Shuffle | `Shuffle` | View clickable | [874,244][1684,334] | 1279,289 |

## List (ScrollView y 352..2260)
- Moi row: `android.view.View` (row dau) HOAC `android.widget.ImageView`, **long-clickable=true**,
  content-desc = `"<Title>\n<unknown> • m:ss"` (CO ky tu " • " + newline).
- Nut 3 cham/download = `Button` con ben phai, bounds x [1612..1720] -> **center x = 1666**.
- Row cao 153px (vd [0,352][1720,505]).
- LUU Y: count "369 tracks" doc bang descriptionContains("tracks") (case-insensitive) + regex
  Java `(\d+)\s*tracks` (descriptionMatches hay truot vi full-match/case-sensitive).
- Title co the chua tieng Viet co dau + emoji (content-desc giu full text).

## Mini player (y 2120..2260)
- Container ImageView content-desc = `"32%, <unknown>"` -> **% tien do nam o day** (vd 32%).
- Title bai: View con content-desc = ten bai, bounds [135,2163][1464,2203].
- Icon 1 (play/pause?): ImageView [1486,2138][1594,2242] -> 1540,2190.
- Icon 2 (queue?):      ImageView [1594,2138][1702,2242] -> 1648,2190.

## Bottom nav (y 2262..2408)
| Tab | content-desc | bounds | center |
|---|---|---|---|
| Home | `Home` | [9,2262][349,2408] | 179,2335 |
| Tracks | `Tracks` | [349,2262][690,2408] | 519,2335 |
| Artists | `Artists` | [690,2262][1030,2408] | 860,2335 |
| Albums | `Albums` | [1030,2262][1371,2408] | 1200,2335 |
| Playlists | `Playlists` | [1371,2262][1711,2408] | 1541,2335 |

## Sort dialog (bottom sheet)
- Title `Sort by` [185,1375][312,1427]; nut X (khong content-desc) [1463,1365][1535,1437] -> center 1499,1401.
- Scrim `Scrim` [0,0][1720,1304].
- 7 option (View clickable, content-desc): Title, Artist, Album, File name, Duration, Date added, Date modified.
- **Option ACTIVE co 1 View CON** (mui ten chieu) ben trai, vd Duration [140,1987][1580,2116] co con [185,2015][257,2087].
  -> isSortActive(name) = xpath `//*[@content-desc='name']/*` (option khac KHONG co con).
- **HANH VI: chon 1 option -> ap dung ngay + DONG dialog.** Muon doc indicator phai mo lai dialog.

## Select mode (long-press 1 bai)
- Header: Back (Button, khong desc) [18,73][126,181] -> 72,127. Select all (ImageView) [1608,73][1716,181] -> 1662,127.
- Nhan dem: content-desc `"0 item selected"` (dang "N item selected") [144,101][1603,153].
  -> isSelectModeActive/getSelectedCount dung descriptionContains("item selected") (KHONG descriptionMatches).
- 4 action (View text, content-desc) o day man: `Add to queue` [155,2356], `Add to list` [592,2356],
  `Share file` [1014,2356], `Delete file` [1429,2356]. + 4 icon ImageView tuong ung o tren (~y2311).
- Row o select mode = View long-clickable, content-desc "Title\n<unknown> • m:ss" (van co " • ").
- Long-press vao select mode hien "0 item selected" (KHONG auto-chon bai vua giu).

## Playing Queue (mo tu icon queue mini player)
- Title content-desc `"Playing Queue"` [36,154][389,226]. Back (Button) content-desc `"Back"` [9,64][117,154].
- Count content-desc `"367 tracks(139/367)"` [36,256][1594,304] -> dung descriptionContains("tracks(") + regex.
- Header phai: shuffle (Button) [1504,64][1612,154] -> 1558,109; repeat [1612,64][1720,154] -> 1666,109.
- Play/pause (ImageView) [1594,226][1702,334] -> 1648,280.
- Row = View, content-desc "ten\n<unknown> • 0:01", nut 3 cham (Button) ben phai [1612..1720].
- Mini player o queue: ImageView content-desc "17%, <unknown>" [0,2269][1720,2408].

## Play Now (full player) - DOM THAT
Container: View content-desc `"Playing now\n<unknown>\n<cur>\n<total>"` (vd "Playing now\n<unknown>\n1:27\n4:28")
bounds [0,0][1720,2408], scrollable+clickable. isPlayNowOpen = descriptionContains("Playing now").
LUU Y: Play Now la OVERLAY -> list Tracks van o tree phia sau (rows() van match row list behind).

Cac nut (Button/ImageView, KHONG content-desc -> tap toa do):
| Element | bounds | center |
|---|---|---|
| Collapse (down) | [0,73][108,181] | **54,127** |
| Menu 3 cham | [1612,73][1720,181] | **1666,127** |
| Album art | [613,244][1108,739] | - |
| Title (View, content-desc=ten bai) | [41,951][1680,1025] | - |
| Heart | [27,1900][135,2008] | 81,1954 |
| Add playlist | [417,1900][525,2008] | 471,1954 |
| Equalizer | [806,1900][914,2008] | 860,1954 |
| Sleep timer | [1196,1900][1304,2008] | 1250,1954 |
| Queue | [1585,1900][1693,2008] | 1639,1954 |
| **SeekBar** (content-desc "32%") | [32,2062][1698,2134] | y 2098 |
| Shuffle | [27,2201][135,2309] | 81,2255 |
| Previous | [383,2183][527,2327] | 455,2255 |
| Play/Pause | [775,2170][946,2341] | 860,2255 |
| Next | [1193,2183][1337,2327] | 1265,2255 |
| Repeat | [1585,2201][1693,2309] | 1639,2255 |
- BUG cu: PN_COLLAPSE_Y=46 (trong status bar) -> collapse truot -> ket Play Now. Da sua = 127.
- SeekBar co content-desc "<n>%" -> co the doc % tien do o Play Now neu can.

## Da xac nhan khop voi TracksPage.java
- SORT_BTN (1657,199), ROW_MENU_X=1666, Play all/Shuffle/count locators: OK.
- rowItems = descriptionContains(" • ") bat dung ca row View lan ImageView, KHONG dinh mini player
  (mini player desc "32%..." / ten bai khong co " • ").
