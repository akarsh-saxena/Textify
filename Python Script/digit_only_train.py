from keras.models import Sequential
from keras.utils import np_utils
import keras.layers as l
import pandas as pd
import numpy as np


train_dataset = pd.read_csv('data/digit_only/mnist_train.csv', header=None)
test_dataset = pd.read_csv('data/digit_only/mnist_test.csv', header=None)

X_train = train_dataset.iloc[:, 1:].values
y_train = train_dataset.iloc[:, 0].values

X_test = test_dataset.iloc[:, 1:].values
y_test = test_dataset.iloc[:, 0].values

X_train = X_train / 255
X_test = X_test / 255

X_train = X_train.reshape(X_train.shape[0], int(X_train.shape[1]**0.5), int(X_train.shape[1]**0.5))
X_test = X_test.reshape(X_test.shape[0], int(X_test.shape[1]**0.5), int(X_test.shape[1]**0.5))

y_train = np_utils.to_categorical(y_train)
y_test = np_utils.to_categorical(y_test)

num_class = y_train.shape[1]

classifier = Sequential()
classifier.add(l.Convolution2D(32, 3, 3, input_shape=(28, 28, 1), activation='relu', kernel_initializer='uniform'))
classifier.add(l.MaxPool2D(pool_size=(2, 2)))
classifier.add(l.Convolution2D(32, 3, 3, activation='relu', kernel_initializer='uniform'))
classifier.add(l.MaxPool2D(pool_size=(2, 2)))
classifier.add(l.Flatten())
classifier.add(l.Dense(64, activation='relu'))
classifier.add(l.Dense(128, activation='relu'))
classifier.add(l.Dense(num_class, activation='softmax'))

classifier.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

X_train = X_train[..., np.newaxis]
X_test = X_test[..., np.newaxis]

classifier.fit(X_train, y_train, batch_size=128, epochs=25, validation_data=(X_test, y_test))

classifier.save('models/digit_only_model')