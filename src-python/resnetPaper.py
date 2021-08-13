from tensorflow.keras.applications.resnet50 import ResNet50
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.resnet50 import preprocess_input, decode_predictions
import numpy as np

from os import listdir
from os.path import isfile, join

# HACK GLOBAL, WHAT THE HELL
model = ResNet50(weights='imagenet')

# Gets the top prediction from ResNet for image f, returns (imageid,prob) tuple.
def predictTop( f ):
    # This handles png and jpg, maybe others.
    img = image.load_img(f, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    preds = model.predict(x)
    # decode the results into a list of tuples (class, description, probability)
    # (one such list for each sample in the batch)
    predictions = decode_predictions(preds, top=1000)[0]
    topPrediction = predictions[0]
    # predictions are tuples with three values
    # ('n02999410', 'chain', 0.20827322)
    imageid = topPrediction[0]
    prob = topPrediction[2]
    return (imageid, prob)

# Gets the top prediction from ResNet for image f, returns (imageid,desc) tuple.
def predictName( f, imageid ):
    # This handles png and jpg, maybe others.
    img = image.load_img(f, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    preds = model.predict(x)
    # decode the results into a list of tuples (class, description, probability)
    # (one such list for each sample in the batch)
    predictions = decode_predictions(preds, top=1000)[0]
    predictionsMap = {}
    for prediction in predictions:
        pname = prediction[0]
        pprob = prediction[2]
        predictionsMap[pname] = pprob
    prob = predictionsMap[imageid]
    return (imageid, prob)
    
def main():
    path = '../images/bird'

    seamPaths = {
        'seam05':'../images/bird_05',
        'seam10':'../images/bird_10',
        'seam15':'../images/bird_15',
        'seam20':'../images/bird_20',
        'seam25':'../images/bird_25'
    }

    # Prepare output file, we always overwrite?
    output = open("myOutFile.txt", "w")
    
    # Go through all the files in the bird folder, these are original ImageNet
    for f in listdir(path):
        if isfile(join(path, f)) and f.endswith('.jpg'):
            print( 'File ' + str(f) )
            # The original ImageNet file is jpg, we save the seams as png
            # Need to rename to get the seam versions
            filename = str(f).replace('.jpg','_rand.png')
            imageid,probability = predictTop( join(path, f) )
            
            m = 'resnet,' + imageid + ',' + str(probability) + '\n'
            output.write( str(f) + '\n')
            output.write( m )
            print( m )

            # Now look the each seam percentage folder and find file
            for (scenario,seampath) in seamPaths.items():
                seamedFile = join( seampath, filename )
                imageid,probability = predictName( seamedFile, imageid )

                m = scenario + ',' + imageid + ',' + str(probability) + '\n'
                print( m )
                output.write( m )
                #print( '    ' + name + ' -> ' + str( prediction ) )
                #print( name + ' -> ' + str(seamedFile) )
    
    # Be nice and close the output file
    output.close()

# Simple prediction that returns tuple (id,name,probability)
def predicted( image, target ):
    img = image.load_img(img_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    preds = model.predict(x)
    # decode the results into a list of tuples (class, description, probability)
    # (one such list for each sample in the batch)
    # [('n01843065', 'jacamar',     0.5692989),
    #  ('n01828970', 'bee_eater',   0.2419548),
    #  ('n01833805', 'hummingbird', 0.08436669)]
    tupleList = decode_predictions(preds, top=1)[0]
    return tupleList[0]
            
    
if __name__ == '__main__':
    main()
