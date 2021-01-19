-- creating a words table 
create table words (
	-- specify the line id that the word is found on
	word_id SERIAL primary key,
	-- specify the word that is guessed
	word text not null,
	definition text not null,
	guessed BOOLEAN not null
);

create table guessedWords (
	guessedWord_id SERIAL primary key,
	word text not null,
	definition text not null,
	word_id INTEGER not null references words(word_id) --foreign key to words table
);

create table longestStreak (
	longestStreak_id SERIAL primary key,
	word text not null,
	word_id INTEGER not null references words(word_id) --foreign key to words table
);

create table dataWords (
	longestWord text not null,
	longestStreakLength integer not null
);

drop table words;
drop table guessedWords;
drop table longestStreak;