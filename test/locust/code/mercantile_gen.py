#!/usr/bin/env python3
import csv
import mercantile

import argparse
import random

parser = argparse.ArgumentParser(description="calculate pyramid bboxes")
parser.add_argument("minx", type=int, help="min X")
parser.add_argument("miny", type=int, help="min Y")
parser.add_argument("maxx", type=int, help="max X")
parser.add_argument("maxy", type=int, help="max Y")
parser.add_argument("minlevel", type=int, help="min pyramid level")
parser.add_argument("maxlevel", type=int, help="max pyramid level")
parser.add_argument("output_filename", help="output filename")
parser.add_argument("-r", "--random", action="store_true", help="randomize lines")
args = parser.parse_args()

# "wms_256_tiles.csv"
                # -77.59899950, 38.53900095, -76.05800395, 39.63099797,
                # range(8, 18)

def write_tile_data():
    filename = args.output_filename
    with open(filename, "w", newline="") as data_file:
        writer = csv.writer(data_file)
        for tile in mercantile.tiles(
                args.minx, args.miny, args.maxx, args.maxy, 
                range(args.minlevel, args.maxlevel)
        ):
            bounds = mercantile.bounds(tile)
            writer.writerow(bounds)


write_tile_data()
if args.random:
    lines=open(args.output_filename).readlines()
    random.shuffle(lines)
    open(args.output_filename,'w').writelines(lines)
