from bs4 import BeautifulSoup
html=open('after_filters.html',encoding='utf-8').read()
soup=BeautifulSoup(html,'html.parser')
th = soup.find('th', string=lambda t: t and 'Процесс' in t)
if not th:
    th = soup.find('th', text=lambda t: t and 'Процесс' in t)
if not th:
    th = soup.find('th', lambda tag: tag.find(string=lambda t: t and 'Процесс' in t) if hasattr(tag,'find') else False)
print(th)
