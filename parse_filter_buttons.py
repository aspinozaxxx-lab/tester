from bs4 import BeautifulSoup
html=open('process_filter.html',encoding='utf-8').read()
btn = BeautifulSoup(html,'html.parser').select('button span')[-1]
print(btn.text)
print(btn.text.encode('unicode_escape').decode())
