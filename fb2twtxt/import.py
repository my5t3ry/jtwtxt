import os
import datetime
import json
import shutil

in_data = 'export/posts/your_posts.json'
writing_dir = 'data/posts'

data = json.load(open(in_data, 'r'))
for filename in os.listdir(writing_dir):
    filepath = os.path.join(writing_dir, filename)
    try:
        shutil.rmtree(filepath)
    except OSError:
        os.remove(filepath)

filename = os.path.join(writing_dir, 'twtxt.txt')

for post in data:
    try:
        ts = datetime.datetime.fromtimestamp(post['timestamp'])
        if len(post['data']) > 0:
            post_text = post['data'][0]['post']
            media_text = ''
            if len(post['attachments']) > 0:
                for post_media in post['attachments']:
                    if 'media' in post_media['data'][0]:
                        uri_split = post_media['data'][0]['media']['uri'].split("/")
                        media_text = media_text + ' @<' + uri_split[len(uri_split) - 1] + ' ' + post_media['data'][0]['media']['uri'] + '>'
                        post_text = post_media['data'][0]['media']['description']
                    if 'external_context' in post_media['data'][0]:
                        media_text = media_text + ' ' + post_media['data'][0]['external_context']['url']
                        if post_media['data'][0]['external_context']['url'] == post_text:
                            post_text = ''
            with open(filename, 'a+') as out_file:
                out_file.write(datetime.datetime.utcfromtimestamp(post['timestamp']).strftime('%Y-%m-%dT%H:%M:%S') + '\t' +
                               post_text.replace('\n', '[LF]').encode('latin1').decode('utf8') + ' ' + media_text + '\n')
    except KeyError:
        print(post)
