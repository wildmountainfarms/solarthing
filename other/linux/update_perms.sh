echo This updates the permission of the solarthing directory. The solarthing directory must be in your current working directory...

chmod 775 -R solarthing/ | exit 1
chmod g+s -R solarthing/ | exit 1
chgrp -R solarthing solarthing/ | exit 1
