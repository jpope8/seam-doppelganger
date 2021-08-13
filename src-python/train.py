import tensorflow as tf
from tensorflow.keras.datasets import cifar10
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.keras.layers import Conv2D, MaxPooling2D

import pickle
import sys

# https://pythonprogramming.net/convolutional-neural-network-deep-learning-python-tensorflow-keras/

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

    # Each convolution and pooling step is a hidden layer. After this, we have
    # a fully connected layer, followed by the output layer. The fully connected
    # layer is your typical neural network (multilayer perceptron) type of layer,
    # and same with the output layer.

    model = Sequential()

    # Note that the X.shape[1:] skips the "-1" feature from prepare.py
    # e.g. shape 100x100x1 if gray or 100x100x3 if color
    # The 256 is the number of filters
    #numfilters = 64
    #kernelsize = 3
    # ! 19.38 accuracy, majority class is 20.00

    numfilters = 64
    kernelsize = 3

    # First ConvNet Needs to know input shape
    model.add(Conv2D(numfilters, (kernelsize, kernelsize), input_shape=X.shape[1:]))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25))

    # Second ConvNet.  Half number of original filters.
    model.add(Conv2D(numfilters//2, (kernelsize, kernelsize)))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25))

    # Third ConvNet.  Half number of original filters.
    model.add(Conv2D(numfilters//2, (kernelsize, kernelsize)))
    model.add(Activation('relu'))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25))

    # This converts our 3D feature maps to 1D feature vectors
    # Must be before the dense layer
    model.add(Flatten())

    # OK, testing shows this to be a real looser, better not to have.
    # First Fully Connected Net (Dense)
    # Debatable, may not help, it is ok to not have a dense layerr before ouput
    # We will find out during optimize
    #model.add(Dense(64))
    #model.add(Activation('relu'))

    # Note that this last layer has to be size of the number of classes
    # e.g. if binary, the 1 is fine, if 7 classes then 7
    # Not sure why, but according to website, use sigmoid for binary, softmax otherwise
    # Thinking about it, sigmoid decides between two (above 0.5, below 0.5)
    # While softmax determines probability for each class and chooses highest (e.g. MAP)
    # https://keras.io/examples/vision/image_classification_from_scratch/
    model.add(Dense(numClasses))
    model.add(Activation('softmax'))

    # We MUST use the SparseCategoricalCrossentropy loss function
    # when we compile the deep CNN (we are not using One-Hot encoding).
    # See https://keras.io/api/losses/probabilistic_losses/#categoricalcrossentropy-class
    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(),
                  optimizer='adam',              # adam, sgd is not so good ihmo
                  metrics=['accuracy'])

    # Batch size is based on number of instances, larger for larger number
    # validation, e.g. 0.1 to 0.5
    model.fit(X, y, batch_size=128, epochs=numEpochs, validation_split=0.3)

if __name__ == '__main__':
    main()
