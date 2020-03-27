echo This updates the permission of the solarthing directory. The solarthing directory must be in your current working directory...

echo This is not ready for use! It screws up some git stuff!
exit 1

chmod 770 -R solarthing/ | exit 1
chmod g+s -R solarthing/ | exit 1
chgrp -R solarthing solarthing/ | exit 1
