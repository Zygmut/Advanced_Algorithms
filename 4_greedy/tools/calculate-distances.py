import json

def calculate_distance(x1:float, y1:float, x2:float, y2:float) -> float:
    return ((x2 - x1)**2 + (y2 - y1)**2)**0.5

def write_json(data, filename='data.json'):
    with open(filename,'w') as f:
        json.dump(data, f, indent=4)

def main(file_name:str):
	with open(file_name) as f:
		data = json.load(f)

	data:list = data.get("graph").get("content")

	for element in data:
		(origin_x, origin_y) = element.get("geoPoint").get("x"), element.get("geoPoint").get("y")

		for connection in element.get("connections"):
			id: int = connection.get("nodeId")

			for node in data:
				if node.get("id") == id:
					(target_x, target_y) = node.get("geoPoint").get("x"), node.get("geoPoint").get("y")
					connection.update({"weight": calculate_distance(origin_x, origin_y, target_x, target_y)})
					break

	write_json(data, "./assets/weighted-data.json")


if __name__ == "__main__":
    main("./assets/ibiza-formentera.json")