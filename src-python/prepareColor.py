import numpy as np
import matplotlib.pyplot as plt
import os
import cv2
from tqdm import tqdm
import sys

# https://pythonprogramming.net/loading-custom-data-deep-learning-python-tensorflow-keras/

# http://image-net.org/api/text/imagenet.synset.geturls?wnid=n11669921

# This is just a utility to see how images are rezsized
def viewImage():
    if( len(sys.argv) != 3 ):
        print( 'Usage: <datadir> <img resize>' )
        return
    datadir = sys.argv[1]
    imgsize = int( sys.argv[2] )
    # Derive categories from the directory
    categories = []
    for category in os.listdir(datadir):
        categories.append(category)

    for category in categories:  # do dogs and cats
        path = os.path.join(datadir,category)  # create path to dogs and cats
        for img in os.listdir(path):  # iterate over each image per dogs and cats
            # convert to array
            #img_array = cv2.imread(os.path.join(path,img) ,cv2.IMREAD_GRAYSCALE)
            img_array = cv2.imread(os.path.join(path,img) ,cv2.IMREAD_COLOR)
            # graph it
            # Strangely, open cv reads in bgr instead of rgb
            # So unless you combine, when displayed you get bluish image
            #plt.imshow(img_array, cmap='gray')
            plt.imshow(img_array)
            # display!
            plt.show()  

            break  # we just want one for now so break
        break  #...and one more!

    # Oh look, a dog!
    print(img_array)

    #And now it's shape:
    print(img_array.shape)

    # So that's a 375 tall, 500 wide, and 3-channel image. 3-channel is
    # because it's RGB (color). We definitely don't want the images that big,
    # but also various images are different shapes, and this is also a problem.
    
    new_array = cv2.resize(img_array, (imgsize, imgsize))
    plt.imshow(new_array)
    plt.show()

# Let's try that. Next, we're going to want to create training data and
# all that, but, first, we should set aside some images for final testing.
# I am going to just manually create a directory called Testing and
# then create 2 directories inside of there, one for Dog and one for Cat.
# From here, I am just going to move the first 15 images from
# both Dog and Cat into the training versions. Make sure you move them,
# not copy. We will use this for our final tests.

# Now, we want to begin building our training data!

training_data = []

# Takes dir of where category directies are located (each directory
# should contain the images).  Since the images are likely of different
# sizes, we need to resize them before pickling.
def create_training_data( datadir, categories, imgsize ):

    for category in categories:  # do dogs and cats

        path = os.path.join(datadir,category)  # create path to dogs and cats
        class_num = categories.index(category)  # get the classification  (0 or a 1). 0=dog 1=cat

        for img in tqdm(os.listdir(path)):  # iterate over each image per dogs and cats
            try:
                # convert to array default cv2.IMREAD_COLOR, IMREAD_GRAYSCALE
                #img_array = cv2.imread(os.path.join(path,img) ,cv2.IMREAD_GRAYSCALE)
                img_array = cv2.imread(os.path.join(path,img) )
                # resize to normalize data size
                new_array = cv2.resize(img_array, (imgsize, imgsize))
                # add this to our training_data
                training_data.append([new_array, class_num])
            except Exception as e:  # in the interest in keeping the output clean...
                pass
            #except OSError as e:
            #    print("OSErrroBad img most likely", e, os.path.join(path,img))
            #except Exception as e:
            #    print("general exception", e, os.path.join(path,img))

# Main entry point.  Takes user argument datadir and finds the categories.
# Then creates model from the images in the datadir/category directory.
# Reizes the images and saves to a pickled file name X and y for category.
# The category is a number (i.e. index) into category names.
def main():
    if( len(sys.argv) != 3 ):
        print( 'Usage: <datadir> <img resize>' )
        return
    datadir = sys.argv[1]
    imgsize = int( sys.argv[2] )
    # Derive categories from the directory
    categories = []
    for category in os.listdir(datadir):
        categories.append(category)

    create_training_data(datadir, categories, imgsize)

    print(len(training_data))


    # Next, we want to shuffle the data. Right now our data is just all dogs,
    # then all cats. This will usually wind up causing trouble too, as,
    # initially, the classifier will learn to just predict dogs always.
    # Then it will shift to oh, just predict all cats! Going back and
    # forth like this is no good either.

    import random

    random.shuffle(training_data)

    # Our training_data is a list, meaning it's mutable, so it's now
    # nicely shuffled. We can confirm this by iterating over a few of
    # the initial samples and printing out the class.
    for sample in training_data[:10]:
        print(sample[1])

    # Great, we've got the classes nicely mixed in! Time to make our model!
    X = []
    y = []

    for features,label in training_data:
        X.append(features)
        y.append(label)

    # Pain in ass, convert to numpy
    # Note the 1 is for grayscale, needs to be 3 for color images
    # The -1 is the "number of features"
    #print(X[0].reshape(-1, imgsize, imgsize, 3))

    X = np.array(X).reshape(-1, imgsize, imgsize, 3)


    # Let's save this data, so that we don't need to keep calculating it
    # every time we want to play with the neural network model:
    import pickle

    pickle_out = open("X.pickle","wb")
    pickle.dump(X, pickle_out)
    pickle_out.close()

    pickle_out = open("y.pickle","wb")
    pickle.dump(y, pickle_out)
    pickle_out.close()

    # We can always load it in to our current script, or a totally new one by doing:
    pickle_in = open("X.pickle","rb")
    X = pickle.load(pickle_in)

    pickle_in = open("y.pickle","rb")
    y = pickle.load(pickle_in)

    # Now that we've got our dataset, we're ready to cover convolutional
    # neural networks and implement one with our data for classification.

    # At very end print index to category mapping
    # This means we MUST use the SparseCategoricalCrossentropy loss function
    # when we compile the deep CNN (we are not using One-Hot encoding).
    # See https://keras.io/api/losses/probabilistic_losses/#categoricalcrossentropy-class
    for category in categories:  # do dogs and cats
        class_num = categories.index(category)  # get the classification  (0 or a 1). 0=dog 1=cat
        print( str(class_num) + ' -> ' + category )

if  __name__ == '__main__':
    #viewImage()
    main()


