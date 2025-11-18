from bs4 import BeautifulSoup
html=open('td_filter_dropdown.html',encoding='utf-8').read()
soup=BeautifulSoup(html,'html.parser')
options = [opt.get_text(strip=True) for opt in soup.select('div.ant-select-item-option')]
print(options)
print([o.encode('unicode_escape').decode() for o in options])
