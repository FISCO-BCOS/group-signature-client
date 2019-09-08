find . -name "*.json" | while read LINE; do dos2unix $LINE; done
find . -name "*.sh" | while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
echo "src/main/resources/node/*"| while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
echo "src/main/resources/sol/*"| while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
echo "src/main/resources/conf/*"| while read LINE; do chmod +x ${LINE};dos2unix ${LINE}; done
