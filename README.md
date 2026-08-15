ssh-keygen -t ed25519 -C "msergiost@hotmail.com"

eval "$(ssh-agent -s)"

ssh-add ~/.ssh/id_ed25519

ssh -T git@github.com

git init

git add .

git remote add origin git@github.com:maurotelles2000/lab.git

git commit -m "first commit"

git push -u origin main


git push --set-upstream origin Alteracao
git push -u origin Alteracao

git config --global user.name "Seu Nome Completo"
git config --global user.email "seu-email@exemplo.com"

----------------------
Configurar certificado no Ubuntu
mkdir -p ~/.ssh

chmod 700 ~/.ssh


cp id_ed25519 ~/.ssh/id_ed25519


chmod 600 ~/.ssh/id_ed25519

eval "$(ssh-agent -s)"

ssh-add ~/.ssh/id_ed25519


Configurar para ficar permanente

nano ~/.ssh/config

Copia para dentro do arquivo

Host github.com
    AddKeysToAgent yes
    IdentityFile ~/.ssh/id_ed25519
    
chmod 600 ~/.ssh/config

ssh -T git@github.com    

sudo chown -R $USER:$USER /home/mauro/shared/workspace/lab
