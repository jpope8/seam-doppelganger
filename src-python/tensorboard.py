import tensorflow as tf
from tensorflow.keras.datasets import cifar10
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.keras.layers import Conv2D, MaxPooling2D
# more info on callbakcs: https://keras.io/callbacks/ model saver is cool too.
from tensorflow.keras.callbacks import TensorBoard
import pickle
from datetime import datetime

import sys


NAME = "ImageNet-CNN-{}".format( datetime.utcnow().strftime('%Y-%m-%d-%H:%M:%S.%f'))

# https://pythonprogramming.net/tensorboard-analysis-deep-learning-python-tensorflow-keras

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

    # Normalize X rgb values, we know are between 0 and 255
    X = X/255.0

    model = Sequential()

    # Memory wise the number of nodes per layer (numfilters) greatly
    # affects the memory usage.   About 64 is max we can handle with 2 ConvNet.
    # We can 4 ConvNet with 32 numfilters.  Deeper 4 ConvNet x 32 may be better
    # anyways than 2 ConvNet x 64 but would be nice to explore for optimization.
    numfilters = 64
    kernelsize = 3
    
    # numfilters = 32, kernelsize = 5, 3 ConvNet w/dropout + Dropout, Output softmax
    # loss: 0.8223 - acc: 0.6946 - val_loss: 1.0769 - val_acc: 0.6036

    # numfilters = 64, kernelsize = 5, 2 ConvNet w/dropout + Dropout Output softmax
    # loss: 1.2357 - acc: 0.5018 - val_loss: 1.3683 - val_acc: 0.4347

    # numfilters = [64,32,32], kernelsize = 3, 3 ConvNet w/dropout + Dropout, Output sigmoid
    # loss: 0.8112 - acc: 0.6982 - val_loss: 0.9965 - val_acc: 0.6216

    # Skip a -1 feature from prepare 
    # First ConvNet Needs to know input shape
    model.add( Conv2D(filters=numfilters, kernel_size=(kernelsize,kernelsize), activation='relu', input_shape=X.shape[1:]))
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25)) # To prevent overfitting, after pooling

    # Second ConvNet.  Do again but do not need input shape this time
    model.add( Conv2D(filters=numfilters//2, kernel_size=(kernelsize,kernelsize), activation='relu') )
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25)) # To prevent overfitting, after pooling

    # Third ConvNet.
    model.add( Conv2D(filters=numfilters//2, kernel_size=(kernelsize,kernelsize), activation='relu') )
    model.add(MaxPooling2D(pool_size=(2, 2)))
    model.add(Dropout(rate=0.25)) # To prevent overfitting, after pooling

    # This converts our 3D feature maps to 1D feature vectors
    # Must be before the dense layer
    model.add(Flatten())

    # OK, testing shows this to be a real looser, better not to have.
    # First Fully Connected Net (Dense)
    # Debatable, may not help, it is ok to not have a dense layerr before ouput
    # We will find out during optimize
    #model.add(Dense(64, activation='relu'))

    # Final output layer, must be of size of number of classes
    model.add(Dropout(rate=0.5))
    model.add(Dense(numClasses, activation='sigmoid')) # activation='sigmoid'

    tensorboard = TensorBoard(log_dir="logs/{}".format(NAME))

    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(),
                  optimizer='adam',
                  metrics=['accuracy'],
                  )

    model.fit(X, y,
              batch_size=128,
              epochs=numEpochs,
              validation_split=0.3,
              callbacks=[tensorboard])

if __name__ == '__main__':
    main()
