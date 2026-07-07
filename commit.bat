@echo off
REM ============================================================
REM  commit.bat - Commit + dong bo len GitHub chi bang 1 lenh
REM  Cach dung:
REM     commit.bat sua loi dang nhap        (khong can dau ngoac)
REM     commit.bat                          (se hoi message)
REM ============================================================
setlocal

REM Ve dung thu muc chua script (goc project)
cd /d "%~dp0"

REM Lay commit message tu tham so, neu khong co thi hoi
set "MSG=%*"
if "%MSG%"=="" set /p "MSG=Nhap commit message: "
if "%MSG%"=="" (
    echo [LOI] Chua co commit message. Dung lai.
    exit /b 1
)

echo.
echo === [1/4] git add -A ===
git add -A

echo.
echo === [2/4] git commit ===
git commit -m "%MSG%"
if errorlevel 1 echo [!] Khong co gi moi de commit - van tiep tuc pull/push de dong bo.

echo.
echo === [3/4] git pull --rebase origin main ===
git pull --rebase origin main
if errorlevel 1 (
    echo.
    echo [LOI] Rebase bi XUNG DOT hoac loi mang.
    echo   - Neu xung dot: sua file, roi chay:  git rebase --continue  va  git push origin main
    echo   - Neu muon huy:                       git rebase --abort
    exit /b 1
)

echo.
echo === [4/4] git push origin main ===
git push origin main
if errorlevel 1 (
    echo.
    echo [LOI] Push that bai - kiem tra mang / dang nhap GitHub.
    exit /b 1
)

echo.
echo === HOAN TAT: da commit va push len GitHub ===
endlocal
