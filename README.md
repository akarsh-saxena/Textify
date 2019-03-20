# Textify

An android app which accepts an image from camera or gallery and with the help of OpenCV, performs some morphological operations in order to improve its quality and returns the handwritten text contained in the image in digital format.

The input image must contain black text on white background. This application, after compressing the image, sends it to the flask server running a python script and receives the recognized text.

On the server side, with the help of morphological operations like dilation, erosion, thresholding etc., it enhances the text and removes the background noise which is then fed into the Convolutional Neural Network which classifies the individual characters.

There are **three models** which are:

1. Capital only model (99.39% accuracy) - **Trained on AZ Dataset** (https://www.kaggle.com/sachinpatel21/az-handwritten-alphabets-in-csv-format). This dataset contains only **capital letters from A to Z** i.e. _26 output classes_.

2. Digits only model (99.43% accuracy). - **Trained on MNIST Dataset** (https://www.kaggle.com/ngbolin/mnist-dataset-digit-recognizer/data). This dataset contains **0-9 digits** i.e. _10 output classes_.

3. Letters and digits model (90.35% accuray). - **Trained on EMNIST by merge dataset** (https://www.kaggle.com/crawford/emnist). This dataset contains **0-9 digits, A-Z letters and some small letters**. It merges the classes of visually similar small and capital letters like c & C, i & I, j and J etc. Hence, it contains a total of 47 classes (More description of the classes are present in the respective links).
