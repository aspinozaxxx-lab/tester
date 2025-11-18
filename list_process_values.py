# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

base_url = 'https://unnd.lab2.pepeshka.ru/'
service = Service(ChromeDriverManager().install())
options = webdriver.ChromeOptions()
options.add_argument('--headless=new')
options.add_argument('--window-size=1920,1080')
driver = webdriver.Chrome(service=service, options=options)
wait = WebDriverWait(driver, 40)

login = lambda: (driver.get(base_url), wait.until(EC.presence_of_element_located((By.ID,'username'))), driver.find_element(By.ID,'username').send_keys('pechenkin'), driver.find_element(By.ID,'password').send_keys('Rctybz1988!'), driver.find_element(By.CSS_SELECTOR,"button[type='submit']").click(), wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'aside.ant-layout-sider'))))

login()

driver.get(base_url + 'userTasks')
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'div.ant-tabs')))
wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR,"div.ant-tabs-tab[data-node-key='inbox']"))).click()
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR,'table tbody tr')))
process_values = []
for row in driver.find_elements(By.CSS_SELECTOR,'table tbody tr'):
    cells = row.find_elements(By.CSS_SELECTOR,'td')
    if cells:
        process_values.append(cells[0].text)
print(process_values)
print([v.encode('unicode_escape').decode() for v in process_values])

driver.quit()
