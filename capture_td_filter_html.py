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

def login():
    driver.get(base_url)
    wait.until(EC.presence_of_element_located((By.ID, 'username')))
    driver.find_element(By.ID, 'username').send_keys('pechenkin')
    driver.find_element(By.ID, 'password').send_keys('Rctybz1988!')
    driver.find_element(By.CSS_SELECTOR, "button[type='submit']").click()
    wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'aside.ant-layout-sider')))

login()

driver.get(base_url + 'registerOfTD')
wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, 'table tbody tr')))
trigger = wait.until(EC.element_to_be_clickable((By.XPATH, "//th[.//span[contains(text(),'Вид')]]//button")))
trigger.click()
wait.until(EC.visibility_of_element_located((By.CSS_SELECTOR, 'div.ant-popover')))
open('td_vid_filter.html','w',encoding='utf-8').write(driver.find_element(By.CSS_SELECTOR,'div.ant-popover').get_attribute('innerHTML'))
print('saved td vid filter')

driver.quit()
