#!/bin/bash
set -e

TARGET_FOLDER="./addons/languages/kasahorow/pack/dictionary/"
DOWNLOAD_URL="https://a.kasahorow.org/android?kasa"

# Add a locale in alphabetical order
KASAHOROW_LOCALES=( \
  "ak" \
  "din" \
  "ee" \
  "ff" \
  "fr" \
  "ge" \
  "ha" \
  "ig" \
  "ki" \
  "wo" \
  "yo" \
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