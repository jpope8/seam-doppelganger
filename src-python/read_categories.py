import sys

# Reads the categories from file mapping name -> description
def readCategories():
    # Using readlines() 
    clslocfile = open('map_clsloc.txt', 'r') 
    lines = clslocfile.readlines() 
      
    count = 0
    # Maps name -> description, e.g. n02226429 631 grasshopper
    categories = {}
    # Strips the newline character 
    for line in lines:
        line = line.strip()
        count += 1
        
        tokens = line.split(' ')
        # n03942813 841 ping-pong_ball
        if(len(tokens) == 3 ):
            name  = tokens[0]
            index = tokens[1]
            descr = tokens[2]
            #print("Line{}: {}".format(count, line))
            categories[name] = descr
    return categories

def main():
    name = sys.argv[1]

    categories = readCategories()

    descr = categories[name]

    print( name + ' -> ' + descr )

if __name__ == '__main__':
    main()
