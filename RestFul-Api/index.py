from flask import Flask,jsonify,json,request
from flask_restful import Resource, Api
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import exc, asc, desc
import datetime
from flask_marshmallow import Marshmallow
from marshmallow import fields
from flask_login import LoginManager, UserMixin, login_user, logout_user, login_required, current_user
from flask_migrate import Migrate

app = Flask(__name__)


app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://pranny:pranjal@localhost/eventsdb'
#Below is the live database, do not use it! Deployed application uses it.
#app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://master:pranjal123@edbinstance.crry28jipnm0.eu-west-2.rds.amazonaws.com/eventsdb'


app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JSON_SORT_KEYS']=False
app.config['SECRET_KEY'] = 'thissecret'

db = SQLAlchemy(app)
ma = Marshmallow(app)
api = Api(app)
login_manager = LoginManager()
login_manager.init_app(app)
migrate = Migrate(app, db)
#Models

#User table
class User(UserMixin,db.Model):
	id= db.Column(db.Integer, primary_key=True)
	username= db.Column(db.String(80), unique=True, nullable=False)
	email= db.Column(db.String(120), unique=True, nullable=False)
	events= db.relationship('Event',  backref='user')
	password = db.Column(db.String(80), nullable=False)
    #
    #
	# def is_active(self):
	# 	#True, as all users are active."""
	# 	return True
    #
	# def get_id(self):
	# 	return self.id
    #
	# def is_authenticated(self):
	# 	"""Return True if the user is authenticated."""
	# 	return self.authenticated
    #
	# def is_anonymous(self):
	# 	"""False, as anonymous users aren't supported."""
	# 	return False
		
#Event Table
class Event(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	event_name = db.Column(db.String(200), nullable=False)
	event_date = db.Column(db.Date(), nullable=False)#yyyy-MM-dd
	event_time = db.Column(db.Time(timezone=False))
	event_location = db.Column(db.String(150))
	event_description = db.Column(db.String(200))
	event_type = db.Column(db.String(30), nullable=False)
	user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
	lists = db.relationship('List', cascade='delete', backref='event')


class List(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	event_id = db.Column(db.Integer, db.ForeignKey('event.id'))
	items = db.relationship('Item', cascade='delete', backref='list')


class Item(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	item_name = db.Column(db.String(250), nullable=False)
	business = db.Column(db.String(500))
	list_id = db.Column(db.Integer, db.ForeignKey('list.id'))

#Schemas
class UserSchema(ma.ModelSchema):
	events = fields.Nested('EventSchema', many=True)

	class Meta:
	#	ordered = True
		model = User

class EventSchema(ma.ModelSchema):
	lists = fields.Nested('ListSchema', many=True)
	class Meta:
	#	ordered = True
		model = Event
		exclude = ('user_id', 'user')

class ListSchema(ma.ModelSchema):
	items = fields.Nested('ItemSchema', many=True)
	class Meta:
		model = List
		exclude = ('id','event_id','event')

class ItemSchema(ma.ModelSchema):
	class Meta:
		model = Item
		exclude = ('list_id','id','list')

#all data
@app.route('/')
def getUserModel():
	users = User.query.all()
	user_schema = UserSchema(many=True)
	# Serialize the queryset
	result = user_schema.dump(users).data
	return jsonify({'users': result})

#create new user
@app.route('/newuser', methods=['POST'])
def new_user():
	try:
		data = request.get_json()
		#print data
		#for i in data['users']:
		new_username = data['username']
		new_email = data['email']
		password = data['password']

		user_exists = User.query.filter_by(username=new_username).count()
		email_exists = User.query.filter_by(email=new_email).count()

		if(user_exists>0 and email_exists>0):
			return jsonify({'response':'Username and Email already in use.'})
		if(user_exists>0):
			return jsonify({'response':'Username already in use.'})
		if(email_exists>0):
			return jsonify({'response':'Email already in use.'})
		new_user = User(username=new_username, email=new_email, password=password)
		db.session.add(new_user)

		db.session.commit()

	except (ValueError, KeyError, TypeError):
		return "JSON format error"

	return jsonify({'response':'Account Created'})


@login_manager.user_loader
def load_user(user_id):
	return User.query.get(int(user_id))

@app.route('/login', methods=['POST'])
def login():
	data = request.get_json()
	try:
		username = data['username']
		password = data['password']
		user_exists = User.query.filter_by(username=username).count()

		if(user_exists>0):
			user = User.query.filter_by(username=username).first()
			if user.username==username and user.password==password:
				login_user(user)
				return jsonify({'response':'You are now logged in!'})

	except (ValueError, KeyError, TypeError):
		return "JSON format error"

	return jsonify({'response':'Username or Password wrong.'})

@app.route('/logintest')
def logintest():
	user = User.query.filter_by(username='group21').first()
	login_user(user)
	return 'You are now logged in!'

@app.route('/logout')
@login_required
def logout():
	logout_user()
	return 'You are now logged out!'

@app.route('/home')
@login_required
def home():
	return 'The current user is ' + current_user.username


#all events of login user
@app.route('/events', methods=['GET'])
@login_required
def get_events():
	#dbUser = User.query.filter_by(username=current_user.username).first()
	# dbUser = User.query.get(current_user.id)
	# print dbUser
	events = current_user.events
	#print events
	events_schema = EventSchema(many=True)
	# Serialize the queryset
	result = events_schema.dump(events).data
	return jsonify({'events': result})

#specific event of login user
@app.route('/event/<int:event_id>', methods=['GET'])
@login_required
def get_event(event_id):
	event = Event.query.filter_by(user_id=current_user.id, id=event_id)
	# all_events = current_user.events
	# selected_event = [event for event in all_events if event.id == event_id]
	# print selected_event
	events_schema = EventSchema(many=True)
	result = events_schema.dump(event).data
	return jsonify({'event': result})

#creating new event
@app.route('/newevent', methods=['POST'])
@login_required
def create_event():
	try:
		data = request.get_json()

		name = data['event_name']
		date = data['event_date']
		time = data['event_time']
		location = data['event_location']
		description = data['event_description']
		type = data['event_type']
		new_event = Event(event_name=name, event_date=date, event_time=time, event_location=location,
						  event_description=description, event_type=type, user_id=current_user.id)

		db.session.add(new_event)

		try:
			print name, date, time, location, description, type
			db.session.commit()

		except exc.OperationalError as e:
			# print("OperationalError")
			print(e)
			return jsonify({'response':'Date or Time format error'})

	except (ValueError, KeyError, TypeError):
		return jsonify({'response':"JSON format error"})

	return jsonify({'response':'Event created'})

#delete event
@app.route('/delete/event/<int:event_id>',methods=['DELETE'])
@login_required
def delete_event(event_id):
	count = Event.query.filter_by(id=event_id, user_id=current_user.id).count()
	if(count>0):
		dEvent = Event.query.filter_by(id=event_id, user_id=current_user.id).first()
		db.session.delete(dEvent)
		db.session.commit()
		return jsonify({'response':'Event Deleted'})

	return jsonify({'response':'Event not found'})

@app.route('/update/event/<int:event_id>',methods=['PUT'])
def update_event(event_id):
	count = Event.query.filter_by(id=event_id, user_id=current_user.id).count()
	if (count > 0):
		data = request.get_json()
		print data
		try:
			name = data['event_name']
			date = data['event_date']
			time = data['event_time']
			location = data['event_location']
			description = data['event_description']
			type = data['event_type']

			uEvent = Event.query.filter_by(id=event_id, user_id=current_user.id).first();
			uEvent.event_name = name
			uEvent.event_date = date
			uEvent.event_time = time
			uEvent.event_location = location
			uEvent.event_description = description
			uEvent.event_type = type

			try:
				db.session.commit()
				#print name,date,time,location
				return jsonify({'response':'Event Updated'})
			except exc.OperationalError as e:
				#print("OperationalError")
				print(e)
				return jsonify({'response':'Date or Time format error'})

		except (ValueError, KeyError, TypeError):
			return jsonify({'response':"JSON format error"})

	return jsonify({'response':'Event not found'})

@app.route('/events/pastevents', methods=['GET'])
@login_required
def past_events():
	#pastEvents = Event.query.filter_by(Event.event_date>'2018-03-15',user_id=current_user.id).all()
	pastEvents = db.session.query(Event).filter(Event.event_date<datetime.date.today(), Event.user_id==current_user.id).order_by(desc(Event.event_date)).all()
	events_schema = EventSchema(many=True)
	result = events_schema.dump(pastEvents).data
	return jsonify({'events': result})

@app.route('/events/upcomingevents', methods=['GET'])
@login_required
def upcoming_events():

	pastEvents = db.session.query(Event).filter(Event.event_date>=datetime.date.today(), Event.user_id==current_user.id).order_by(Event.event_date.asc(),Event.event_time.asc()).all()
	events_schema = EventSchema(many=True)
	result = events_schema.dump(pastEvents).data
	return jsonify({'events': result})

#all lists of login user
@app.route('/lists', methods=['GET'])
@login_required
def get_lists():
	event = Event.query.filter_by(user_id=current_user.id).first()
	#print event
	list = event.lists
	list_schema = ListSchema(many=True)
	# Serialize the queryset
	result = list_schema.dump(list).data
	return jsonify({'lists':result})

#create a list
@app.route('/event/<int:event_id>/createlist', methods=['POST'])
@login_required
def create_list(event_id):
	count = Event.query.filter_by(id=event_id, user_id=current_user.id).count();
	if(count>0):
		list_Event = Event.query.filter_by(id=event_id, user_id=current_user.id).first();
		new_list = List(event=list_Event)
		db.session.add(new_list)
		db.session.commit()
		return 'List Created'

	return 'Event not found'

#delete a list
@app.route('/event/<int:event_id>/deletelist/<int:list_id>', methods=['DELETE'])
@login_required
def delete_list(event_id,list_id):
	count = (db.session.query(List).join(Event).filter(List.id == list_id, List.event_id == event_id).filter(
		Event.user_id == current_user.id)).count()

	print count
	if (count > 0):
		list = (db.session.query(List).join(Event).filter(List.id == list_id, List.event_id == event_id).filter(
			Event.user_id == current_user.id)).first()
		db.session.delete(list)
		db.session.commit()

		return 'List Deleted'

	return 'No list found'


#get one list
@app.route('/event/<int:event_id>/list/<int:list_id>', methods=['GET'])
@login_required
def get_items(event_id,list_id):
	list = (db.session.query(Item).join(List).join(Event).
				 filter(Item.list_id==list_id).filter(List.event_id==event_id).filter(Event.user_id==current_user.id)).all()
#	print list
	item_schema = ItemSchema(many=True)
	# Serialize the queryset
	result = item_schema.dump(list).data
	return jsonify({'Items':result})


#add items,delete,update
@app.route('/event/<int:event_id>/list/<int:list_id>/updateitems', methods=['POST'])
@login_required
def add_items(event_id,list_id):
	#count = (List.query.filter_by(id=list_id).join(Event, Event.user_id==current_user.id)).count()
	#count2= db.session.query(List, Event).select_from(join(List, Event)).filter(List.id==list_id, Event.user_id==current_user.id).all()
	#q = (db.session.query(Event, List).filter(Event.user_id==current_user.id).filter(List.id == list_id, List.event_id==event_id)).all()

	count = (db.session.query(List).join(Event).filter(List.id == list_id, List.event_id==event_id).filter(Event.user_id==current_user.id)).count()
	#print count

	if(count>0):
		# items = Item.query.filter_by(list_id=list_id).join(List, List.event_id == event_id).join(Event, Event.user_id == current_user.id).all()
		# print items
		items = (db.session.query(Item).join(List).join(Event).
				 filter(Item.list_id==list_id).filter(List.event_id==event_id).filter(Event.user_id==current_user.id)).all()
		#print items
		for j in items:
			db.session.delete(j)

		# list2= db.session.query(Item).filter(Item.list_id==list_id).join(List, List.event_id==event_id).join(Event, Event.user_id==current_user.id).delete()
		# Item.query.filter_by(list_id=list_id).update({Item.item_name:'testing'})

		# list2 = List.query.filter_by(id=list_id, event_id=event_id).join(Event,
		# 																 Event.user_id == current_user.id).first()
		# print list2

		list = (db.session.query(List).join(Event).filter(List.id == list_id, List.event_id == event_id).filter(
			Event.user_id == current_user.id)).first()
		#print list


		data = request.get_json()
		for i in data['items']:
			name = i['item_name']
			business = i['business']
			# print name,business
			new_item = Item(item_name=name, business=business, list=list)
			db.session.add(new_item)

		db.session.commit()

		return 'List items added'

	return 'List not found'


if __name__ == '__main__':
	app.run(debug=True)