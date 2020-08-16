#!/bin/bash
set -e

TARGET_FOLDER="./addons/languages/kasahorow/pack/dictionary/"
DOWNLOAD_URL="https://t9n-20200808-dot-fienipadict.appspot.com/android?kasa"

KASAHOROW_LOCALES=( \
  "ge" \
  "din" \
  )

for locale in "${KASAHOROW_LOCALES[@]}"
do
  echo "Downloading wordlist for ${locale}"
  combined_name="${locale}_wordlist.combined"
  output_file="/tmp/${combined_name}"
  wget --tries=5 --waitretry=5 --progress=dot:giga --output-document="${output_file}" "${DOWNLOAD_URL}=${locale}"
  echo "Done downloading wordlist!"
  echo "Making a ${combined_name} file"
  gzip -c "${output_file}" > "${output_file}.gz"
  mv "${output_file}.gz" $TARGET_FOLDER
  echo "Done making ${combined_name}.gz file"
done

echo "Now making dictionary"
./gradlew clean :addons:languages:kasahorow:pack:makeDictionary
echo "Done making dictionary!"