

# set the matplotlib backend so figures can be saved in the background
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import numpy as np

# import the necessary packages
from sklearn.preprocessing import LabelBinarizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sklearn.model_selection import train_test_split


import tensorflow as tf
from tensorflow.keras.datasets import cifar10
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten
from tensorflow.keras.layers import Conv2D, MaxPooling2D
import pickle
from datetime import datetime

import sys


NAME = "ImageNet-CNN-{}".format( datetime.utcnow().strftime('%Y-%m-%d-%H:%M:%S.%f'))

# https://pythonprogramming.net/tensorboard-analysis-deep-learning-python-tensorflow-keras

# Normalize tutorial
# https://machinelearningmastery.com/how-to-normalize-center-and-standardize-images-with-the-imagedatagenerator-in-keras/

# Another nice tutorial
# https://towardsdatascience.com/introduction-to-deep-learning-with-keras-17c09e4f0eb2

# Really good intro to CNNs
# https://towardsdatascience.com/simple-introduction-to-convolutional-neural-networks-cdf8d3077bac

# Canonocal intro to CNN vis Stanford
# https://cs231n.github.io/convolutional-networks/

# Very nice intro to CNN with training/test matplot lib
# https://www.pyimagesearch.com/2018/09/10/keras-tutorial-how-to-get-started-with-keras-deep-learning-and-python/


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


    # Split into train and test sets (these are numpy arrays I believe)
    #X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)
    trainX, testX, trainY, testY = train_test_split(X, y, test_size=0.3)
        

    # confirm scale of pixels
    #print('Train min=%.3f, max=%.3f' % (trainX.min(), trainX.max()))
    #print('Test min=%.3f, max=%.3f' % (testX.min(), testX.max()))


    # Use generator to standardize and generate images
    

    # Normalize X rgb values, we know are between 0 and 255
    #X = X/255.0
    #datagen = ImageDataGenerator(rotation_range=20, zoom_range=0.15,
	#            width_shift_range=0.2, height_shift_range=0.2, shear_range=0.15,
	#            horizontal_flip=True, fill_mode="nearest", rescale=1.0/255.0)
    datagen = ImageDataGenerator(rescale=1.0/255.0)

    # prepare an iterators to scale images
    train_iterator = datagen.flow(trainX, trainY, batch_size=64)
    test_iterator = datagen.flow(testX, testY, batch_size=64)

    # confirm the scaling works, short answer, it works
    #batchX, batchy = train_iterator.next()
    #print('Batch shape=%s, min=%.3f, max=%.3f' % (batchX.shape, batchX.min(), batchX.max()))


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
    # Debatable, may not help, it is ok to not have a dense layer before ouput
    # We will find out during optimize
    #model.add(Dense(64, activation='relu'))

    # Final output layer, must be of size of number of classes
    model.add(Dropout(rate=0.5))
    model.add(Dense(numClasses, activation='softmax'))

    # Not sure why, but according to website, use sigmoid for binary, softmax otherwise
    # https://keras.io/examples/vision/image_classification_from_scratch/
    # Thinking about it, sigmoid decides between two (above 0.5, below 0.5)
    # While softmax determines probability for each class and chooses highest (e.g. MAP)

    
    model.compile(loss=tf.keras.losses.SparseCategoricalCrossentropy(),
                  optimizer='adam',
                  metrics=['accuracy'],
                  )


    # fit model with generator
    # I prefer the validation_split=0.3 for the fit method.  Specifying same
    # testX and testy seems wrong to me, we will use to "evaluate" below.
    # https://www.pyimagesearch.com/2018/12/24/how-to-use-keras-fit-and-fit_generator-a-hands-on-tutorial/
    #H = model.fit_generator(train_iterator, steps_per_epoch=len(train_iterator), epochs=numEpochs)
    #H = model.fit(train_iterator, batch_size=64, validation_split=0.3, epochs=numEpochs)
    
    # This seems to go one instance at a time within epoch
    # Not sure why this does not work??? validation_data=(testX, testY)
    H = model.fit_generator(train_iterator,
                            steps_per_epoch=5,
                            epochs=numEpochs)

    # evaluate model
    _, acc = model.evaluate_generator(test_iterator, steps=len(test_iterator), verbose=0)
    print('Test Accuracy: %.3f' % (acc * 100))


    # Having problems, the fit_generator should have validation data with it
    # so it can generate a generalization error for each epoch.  Damned if
    # I cannot figure out how to do this.
    print( 'H keys: ' + str( H.history.keys() ) )

    # plot the training loss and accuracy
    N = np.arange(0, numEpochs)
    plt.style.use("ggplot")
    plt.figure()
    plt.plot(N, H.history["loss"], label="train_loss")
    plt.plot(N, H.history["val_loss"], label="val_loss")
    plt.plot(N, H.history["accuracy"], label="train_acc")
    plt.plot(N, H.history["val_accuracy"], label="val_acc")
    plt.title("Training Loss and Accuracy (Simple NN)")
    plt.xlabel("Epoch #")
    plt.ylabel("Loss/Accuracy")
    plt.legend()
    plt.savefig(args["plot"])

    
    


if __name__ == '__main__':
    main()
