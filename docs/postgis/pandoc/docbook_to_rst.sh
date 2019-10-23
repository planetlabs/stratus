# Modified from https://gist.github.com/hugorodgerbrown/5317616
#
# This script was created to convert a directory full
# of .xml docbook files into rst equivalents. It uses
# pandoc to do the conversion.
#
# 1. Install pandoc from http://johnmacfarlane.net/pandoc/
# 2. Copy this script into the directory with the .xml pandoc files 
# 3. Ensure that the script has execute permissions
# 4. Run the script
#
# By default this will keep the original .xml files

# Create index.rst for toc and homepage
echo '.. toctree::\n   :maxdepth: 2' >> index.rst

FILES=*.xml
for f in $FILES
do
  # extension="${f##*.}"
  filename="${f%.*}"
  echo "Converting $f to $filename.rst"
  `pandoc --from docbook --to rst $f -o ./$filename.rst` 
  # now write filename to index.rst for toc
  echo '\t'$filename >> index.rst
done

# now move .rst files to new directory
`mkdir -p rst_src`
echo "Moving .rst files into directory rst_src"
`mv *.rst rst_src`
echo "Copying images directory into directory rst_src"
# now copy images directory
`cp -R images rst_src`
