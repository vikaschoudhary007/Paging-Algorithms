create-venv:
	conda create -n cache-py --force -y

install:
	pip install -r requirements.txt

run-test:
	pytest --cov=src --cov-report html -v


generate-plots:
	python src/main.py
