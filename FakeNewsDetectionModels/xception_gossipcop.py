import numpy as np
import data
from tensorflow.keras.applications.xception import Xception
from tensorflow.keras.applications.xception import preprocess_input
from tensorflow.keras.models import Model

base_model = Xception(weights='imagenet')
model = Model(inputs=base_model.input, outputs=base_model.get_layer(base_model.layers[-2].name).output)


dataset = 'D:/Users/Vagelis/PycharmProjects/fake_news_dataset_final/gossipcop'
fake_images, real_images = data.load_images(dataset, (299, 299))

# extract image features
fake_images = preprocess_input(fake_images)
fake_image_features = model.predict(fake_images)
real_images = preprocess_input(real_images)
real_image_features = model.predict(real_images)

image_features = np.concatenate((fake_image_features, real_image_features))
np.save(dataset + '/xception', image_features)
