# https://keras.io/api/applications/resnet/#resnet50-function

import tensorflow as tf
from keras import applications

import pickle
import sys

def main():
    if( len(sys.argv) != 2 ):
        print( 'Usage: <num epochs>' )
        return

    numEpochs = int( sys.argv[1] )

    pickle_in = open("X.pickle","rb")
    X = pickle.load(pickle_in)

    pickle_in = open("y.pickle","rb")
    y = pickle.load(pickle_in)

    # Find out the number of classes so we can set the output layer properly
    numClasses = len(set(y))
    print( 'NUMBER CLASSES = {}'.format( numClasses ) )

    # Normalize, may also want to look at keres.utils.normalize
    X = X/255.0


    model = applications.resnet50.ResNet50(
                    weights= None,
                    include_top=True,
                    input_shape= (224,224,3),
                    pooling="max",
                    classes=numClasses
                )

    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(),
                  optimizer='adam',              # adam, sgd is not so good ihmo
                  metrics=['accuracy'])

    model.fit(X, y, batch_size=128, epochs=numEpochs, validation_split=0.3)


if __name__ == '__main__':
    main()

