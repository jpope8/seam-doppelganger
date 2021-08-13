import numpy as np
import matplotlib.pyplot as plt
import os
import cv2
from tqdm import tqdm

DATADIR = "./TrainPetImages"

CATEGORIES = ["Dog", "Cat"]

IMG_SIZE = 100

# https://pythonprogramming.net/loading-custom-data-deep-learning-python-tensorflow-keras/

# This is just a utility to see how images are rezsized
def viewImage():
    for category in CATEGORIES:  # do dogs and cats
        path = os.path.join(DATADIR,category)  # create path to dogs and cats
        for img in os.listdir(path):  # iterate over each image per dogs and cats
            img_array = cv2.imread(os.path.join(path,img) ,cv2.IMREAD_GRAYSCALE)  # convert to array
            plt.imshow(img_array, cmap='gray')  # graph it
            plt.show()  # display!

            break  # we just want one for now so break
        break  #...and one more!

    # Oh look, a dog!
    print(img_array)

    #And now it's shape:
    print(img_array.shape)

    # So that's a 375 tall, 500 wide, and 3-channel image. 3-channel is
    # because it's RGB (color). We definitely don't want the images that big,
    # but also various images are different shapes, and this is also a problem.

    IMG_SIZE = 50

    new_array = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE))
    plt.imshow(new_array, cmap='gray')
    plt.show()

    # Hmm, that's a bit blurry I'd say. Let's go with 100x100?

    IMG_SIZE = 100

    new_array = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE))
    plt.imshow(new_array, cmap='gray')
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

def create_training_data():
    for category in CATEGORIES:  # do dogs and cats

        path = os.path.join(DATADIR,category)  # create path to dogs and cats
        class_num = CATEGORIES.index(category)  # get the classification  (0 or a 1). 0=dog 1=cat

        for img in tqdm(os.listdir(path)):  # iterate over each image per dogs and cats
            try:
                # convert to array
                img_array = cv2.imread(os.path.join(path,img) ,cv2.IMREAD_GRAYSCALE)
                # resize to normalize data size
                new_array = cv2.resize(img_array, (IMG_SIZE, IMG_SIZE))
                # add this to our training_data
                training_data.append([new_array, class_num])
            except Exception as e:  # in the interest in keeping the output clean...
                pass
            #except OSError as e:
            #    print("OSErrroBad img most likely", e, os.path.join(path,img))
            #except Exception as e:
            #    print("general exception", e, os.path.join(path,img))

create_training_data()

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
print(X[0].reshape(-1, IMG_SIZE, IMG_SIZE, 1))

X = np.array(X).reshape(-1, IMG_SIZE, IMG_SIZE, 1)


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











