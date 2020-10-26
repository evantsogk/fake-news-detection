import multimodal_model
import text_model
import pickle

dataset_politifact = 'D:/Users/Vagelis/PycharmProjects/fake_news_dataset_final/politifact'
dataset_gossipcop = 'D:/Users/Vagelis/PycharmProjects/fake_news_dataset_final/gossipcop'
save_path = 'models'

"""
model, tokenizer = multimodal_model.train(dataset_politifact)
model.save(save_path + '/multimodal_politifact.h5')
with open(save_path + '/tokenizer_politifact.pickle', 'wb') as handle:
    pickle.dump(tokenizer, handle, protocol=pickle.HIGHEST_PROTOCOL)
"""

"""
model, tokenizer = multimodal_model.train(dataset_gossipcop)
model.save(save_path + '/multimodal_gossipcop.h5')
with open(save_path + '/tokenizer_gossipcop.pickle', 'wb') as handle:
    pickle.dump(tokenizer, handle, protocol=pickle.HIGHEST_PROTOCOL)
"""

"""
model, _ = text_model.train(dataset_politifact)
model.save(save_path + '/text_politifact.h5')
"""

"""
model, _ = text_model.train(dataset_gossipcop)
model.save(save_path + '/text_gossipcop.h5')
"""