ssh-keygen -t ed25519 -C "msergiost@hotmail.com"

eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
ssh -T git@github.com

git init
git add .
git remote add origin git@github.com:maurotelles2000/lab.git
git commit -m "first commit"
git push -u origin main
