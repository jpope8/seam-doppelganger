from os import listdir
from os.path import isfile, join
import sys

# Parses the line 'seam10,n01824575,0.020001188'
#   return tuple with probability as a float
def parse( s ):
    tokens = s.split(',')
    if( len(tokens) != 3 ):
        print( 'Line not three tokens:' + str(s) )
    return tokens[0], tokens[1], float(tokens[2])

def main():
    filename = sys.argv[1]
    
    # Using readlines() 
    experimentFile = open(filename, 'r') 
    lines = experimentFile.readlines() 
      
    count = 0
    # Maps scenario -> total
    # Simpler to just use list
    #         'resnet':0.0, 'seam05':0.0, 'seam10':0.0, 'seam15':0.0, 'seam20':0.0, 'seam25':0.0
    scenarios=['resnet', 'seam05', 'seam10', 'seam15', 'seam20', 'seam25']
    totals = [         0.0,          0.0,          0.0,          0.0,          0.0,          0.0 ]
    # Strips the newline character 
    for i in range( len(lines) ):
        lines[i] = lines[i].strip()

    # Now for each original file process each scenario
    for i in range( len(lines) ):
        line = lines[i]
        if line.endswith('.jpg'):
            count += 1
            # Each line has scenario, imageid, probability
            # 3179027643_3386823c9d.jpg
            # resnet,n01824575,0.35992524
            # seam05,n01824575,0.017757358
            # seam10,n01824575,0.020001188
            # seam15,n01824575,0.0031304078
            # seam20,n01824575,0.0023694967
            # seam25,n01824575,0.002718497

            resnet = parse(lines[i+1]) # original
            scenario = resnet[0]
            imageid  = resnet[1]
             
            seam05 = parse(lines[i+2]) # 0.05
            seam10 = parse(lines[i+3]) # 0.10
            seam15 = parse(lines[i+4]) # 0.15
            seam20 = parse(lines[i+5]) # 0.20
            seam25 = parse(lines[i+6]) # 0.25          

            # Now add to running total
            totals[0] += resnet[2]
            totals[1] += seam05[2]
            totals[2] += seam10[2]
            totals[3] += seam15[2]
            totals[4] += seam20[2]
            totals[5] += seam25[2]
    
    # Now take average by dividing by the count
    # Print out
    print( 'Count = ' + str(count) )
    for i in range( len(totals) ):
        avg = totals[i] / count
        print( scenarios[i] + ' = ' + str(avg) )

    # Be nice and close the output file
    experimentFile.close()
    
if __name__ == '__main__':
    main()
