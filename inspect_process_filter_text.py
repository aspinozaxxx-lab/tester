text=open('process_filter.html',encoding='utf-8').read()
print(text.encode('unicode_escape').decode())
