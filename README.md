ssh-keygen -t ed25519 -C "msergiost@hotmail.com"


eval "$(ssh-agent -s)"

ssh-add ~/.ssh/id_ed25519

ssh -T git@github.com