from tensorflow.keras.applications.resnet50 import ResNet50
from tensorflow.keras.preprocessing import image
from tensorflow.keras.applications.resnet50 import preprocess_input, decode_predictions
import numpy as np
import sys

def main():
    if( len(sys.argv) != 2 ):
        print( 'Usage: <image>' )
        return

    model = ResNet50(weights='imagenet')

    img_path = sys.argv[1]
    #img_path = './TrainImageNet/bird/3883631811_cd78fd3e04.jpg'
    # This handles png and jpg, maybe others.
    img = image.load_img(img_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    preds = model.predict(x)
    # decode the results into a list of tuples (class, description, probability)
    # (one such list for each sample in the batch)
    predictions = decode_predictions(preds, top=1000)[0]
    #for prediction in predictions:
    #    print( str(prediction) )

    #topPrediction = predictions[0] # predictions are tuples with three values
    #print( str(topPrediction[0]) )
    #print( str(topPrediction[1]) )
    #print( str(topPrediction[2]) )

    print('Predicted:', decode_predictions(preds, top=3)[0])
    # ('n03000134', 'chainlink_fence', 0.5927864)
    # ('n02999410', 'chain', 0.20827322)
    # ('n03000247', 'chain_mail', 0.12105631)
    # Predicted: [(u'n02504013', u'Indian_elephant', 0.82658225), (u'n01871265', u'tusker', 0.1122357), (u'n02504458', u'African_elephant', 0.061040461)]

if __name__ == '__main__':
    main()
