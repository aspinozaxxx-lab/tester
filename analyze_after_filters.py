from bs4 import BeautifulSoup
html=open('after_filters.html',encoding='utf-8').read()
soup=BeautifulSoup(html,'html.parser')
rows = soup.select('table tbody tr')
print('rows count', len(rows))
if rows:
    print('first row text', rows[0].get_text())
print('placeholders', soup.select('div.ant-empty-description'))
print('placeholders2', soup.select('div.ant-empty'))
print('table status nodes', soup.select('div.ant-table-empty'))
