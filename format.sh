find . -name "*.json" | while read LINE; do dos2unix $LINE; done
find . -name "*.sh" | while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
echo "sig_client/src/main/executive/shell/*"| while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
echo "sig_client/src/main/executive/sol/*"| while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
